package io.github.sdxqw.pong2.server;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.score.Score;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PongServer {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/pong";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123";
    private Connection connection;

    public PongServer(PongGame game) {
        Thread threadConnection = new Thread(this::connectToDatabase);
        threadConnection.start();

        Thread threadValidation = new Thread(() -> validateConnection(game));
        threadValidation.start();
    }

    private void validateConnection(PongGame game) {
        while (true) {
            try {
                Thread.sleep(5000); // Wait for 5 seconds between each validation check

                Connection conn = getConnection();
                if (conn != null && conn.isValid(5)) {
                    System.out.println("Database connection is valid");
                } else {
                    System.err.println("Database connection is not valid, reconnecting...");
                    connectToDatabase();

                    // Update the user name when the database connection changes
                    UUID sessionID = game.userData.getSessionID();
                    if (sessionID != null) {
                        loadSessionFromDatabase(game, sessionID);
                    }
                }
            } catch (InterruptedException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToDatabase() {
        int maxAttempts = 5; // Maximum number of connection attempts
        int attempt = 0;

        while (attempt < maxAttempts) {
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Connected to the database");
                return; // Connection successful, exit the loop
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
                attempt++;
                System.out.println("Retrying connection (" + attempt + "/" + maxAttempts + ")...");
                try {
                    Thread.sleep(1000); // Wait for 1 second before retrying
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.err.println("Failed to connect to the database");
    }

    public synchronized Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void closeConnection() {
        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSessionFromDatabase(PongGame game, UUID sessionID) {
        Connection conn;

        while ((conn = getConnection()) == null) {
            try {
                Thread.sleep(1000); // Wait for 1 second before trying again
                handleNoDatabaseConnection(game);
            } catch (InterruptedException e) {
                handleNoDatabaseConnection(game);
                e.printStackTrace();
            }
        }


        // Database connection is available, load session data from the database
        String query = "SELECT username, highest_scores FROM Users WHERE session_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, sessionID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    int highestScores = resultSet.getInt("highest_scores");

                    // Update the game state with the loaded session data
                    updateGameState(game, username, highestScores);
                } else {
                    // No session data found in the database, handle the case
                    handleNoSessionID(game);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Error occurred during database query, handle the case
            handleNoDatabaseConnection(game);
        }
    }

    public void handleNoDatabaseConnection(PongGame game) {
        // Load session data from the local UserData
        game.userData.loadSessionID();

        // Update the game state with the loaded session data
        updateGameState(game, "User" + (Math.abs(new Random().nextInt()) % 100 + 1), 0);
    }

    public void handleNoSessionID(PongGame game) {
        // Generate a new session ID
        UUID sessionID = UUID.randomUUID();
        game.userData.setSessionID(sessionID);

        // Update the game state with the new session data
        updateGameState(game, "User" + new Random().nextInt(100) + 1, 0);
    }

    public List<String> getAllUserNames() {
        List<String> usernames = new ArrayList<>();
        Connection conn = getConnection(); // Get the connection

        if (conn == null) {
            // Handle the case where there is no database connection
            return usernames;
        }

        try (PreparedStatement statement = conn.prepareStatement("SELECT username FROM Users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                usernames.add(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usernames;
    }

    public List<Integer> getHighestScores() {
        List<Integer> highestScores = new ArrayList<>();
        Connection conn = getConnection(); // Get the connection

        if (conn == null) {
            // Handle the case where there is no database connection
            return highestScores;
        }

        try (PreparedStatement statement = conn.prepareStatement("SELECT highest_scores FROM Users");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int score = resultSet.getInt("highest_scores");
                highestScores.add(score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return highestScores;
    }


    public void saveSessionToDatabase(String userName, Score score, UUID sessionID) {
        Connection conn = getConnection(); // Get the connection

        if (conn == null || sessionID == null) {
            // Handle the case where there is no database connection or session ID is not available
            return;
        }

        // Perform the save operation
        try {
            // Check if the session ID already exists in the database
            String checkQuery = "SELECT highest_scores FROM Users WHERE session_id = ?";
            try (PreparedStatement checkStatement = conn.prepareStatement(checkQuery)) {
                checkStatement.setString(1, sessionID.toString());
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int currentHighestScore = resultSet.getInt("highest_scores");
                        int newHighestScore = score.getHighest();
                        if (newHighestScore > currentHighestScore) {
                            // New score is higher, update the existing record
                            String updateQuery = "UPDATE Users SET username = ?, highest_scores = ? WHERE session_id = ?";
                            try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                                updateStatement.setString(1, userName);
                                updateStatement.setInt(2, newHighestScore);
                                updateStatement.setString(3, sessionID.toString());

                                updateStatement.executeUpdate();
                            }
                        }
                    } else {
                        // Session ID doesn't exist, insert a new record
                        String insertQuery = "INSERT INTO Users (session_id, username, highest_scores) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                            insertStatement.setString(1, sessionID.toString());
                            insertStatement.setString(2, userName);
                            insertStatement.setInt(3, score.getHighest());

                            insertStatement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateGameState(PongGame game, String username, int highestScores) {
        game.userName = username;
        game.score.highestScore = highestScores;
    }
}
