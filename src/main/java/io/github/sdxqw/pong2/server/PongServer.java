package io.github.sdxqw.pong2.server;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.score.Score;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PongServer {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/pong";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123";

    private Connection connection;

    public PongServer() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error executing SQL statement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSessionFromDatabase(PongGame game, UUID sessionID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT username, highest_scores FROM Users WHERE session_id = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, sessionID.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        int highestScores = resultSet.getInt("highest_scores");

                        // Update the game state with the loaded session data
                        updateGameState(game, username, highestScores);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveSessionToDatabase(String userName, Score score, UUID sessionID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
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

    public List<String> getAllUserNames() {
        List<String> usernames = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT username FROM Users";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String username = resultSet.getString("username");
                        usernames.add(username);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usernames;
    }

    public List<Integer> getHighestScores() {
        List<Integer> highestScores = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT highest_scores FROM Users";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int score = resultSet.getInt("highest_scores");
                        highestScores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return highestScores;
    }

    private void updateGameState(PongGame game, String username, int highestScores) {
        game.userName = username;
        if (highestScores > game.score.highestScore) {
            game.score.highestScore = highestScores;
        }
    }
}
