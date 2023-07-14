package io.github.sdxqw.pong2.server;

import io.github.sdxqw.logger.Logger;
import io.github.sdxqw.pong2.PongGame;
import lombok.Getter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public class PongServer {
    private final PongServerInfo dbInfo = PongServerInfo.Database;
    private final PongGame game;
    private Connection connectionPool = null;
    private volatile boolean connectionAlive = false;

    public PongServer(PongGame game) {
        this.game = game;
        Logger.info("Starting PongServer...");
        Logger.info("DataBase URL: %s", dbInfo.getDbUrl());
        Logger.info("DataBase User: %s", dbInfo.getDbUser());
        Logger.info("DataBase Password: %s", dbInfo.getDbPassword());

        try {
            connectionPool = DriverManager.getConnection(dbInfo.getDbUrl(), dbInfo.getDbUser(), dbInfo.getDbPassword());
            if (!connectionPool.isClosed() || connectionPool != null) {
                connectionAlive = true;
                Logger.info("Server started successfully!");
            } else {
                connectionAlive = false;
                Logger.error("Failed to initialize connection pool: %s", dbInfo.getDbUrl());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connectionPool != null) {
            try {
                Logger.info("Closing connection to database: %s", dbInfo.getDbUrl());
                connectionAlive = false;
                connectionPool.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ResultSet readFromDatabase(UUID sessionID) {
        if (!isConnectionAlive())
            return null;

        try {
            PreparedStatement statement = connectionPool.prepareStatement("SELECT * FROM Users WHERE session_id = ?");
            statement.setString(1, sessionID.toString());
            return statement.executeQuery();
        } catch (SQLException e) {
            return null;
        }
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();

        if (!isConnectionAlive()) {
            return users;
        }

        try {
            PreparedStatement statement = connectionPool.prepareStatement("SELECT username FROM Users");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String userName = resultSet.getString("username");
                users.add(userName);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve users from the database", e);
        }

        return users;
    }

    public List<String> getAllHighestScores() {
        List<String> scores = new ArrayList<>();

        if (!isConnectionAlive()) {
            return scores;
        }

        try {
            PreparedStatement statement = connectionPool.prepareStatement("SELECT highest_scores FROM Users");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String highScore = resultSet.getString("highest_scores");
                scores.add(highScore);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve highest scores from the database", e);
        }

        return scores;
    }


    public void getUserName(UUID sessionID) {
        if (!isConnectionAlive()) {
            game.userData.setUserName("Player" + Math.abs(new Random().nextInt() & 100) + 1);
        } else {
            ResultSet resultSet = readFromDatabase(sessionID);
            if (resultSet != null) {
                try {
                    if (resultSet.next()) {
                        game.userData.setUserName(resultSet.getString("username"));
                    } else {
                        game.userData.setUserName("Player" + Math.abs(new Random().nextInt() & 100) + 1);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to retrieve username from ResultSet", e);
                } finally {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        Logger.warn("Failed to close ResultSet", e);
                    }
                }
            } else {
                game.userData.setUserName("Player" + Math.abs(new Random().nextInt() & 100) + 1);
            }
        }
    }

    public void getHighestScore(UUID sessionID) {
        if (!isConnectionAlive()) {
            game.userData.setHighScore("0");
        } else {
            ResultSet resultSet = readFromDatabase(sessionID);
            if (resultSet != null) {
                try {
                    if (resultSet.next()) {
                        game.userData.setHighScore(resultSet.getString("highest_scores"));
                    } else {
                        game.userData.setHighScore("0");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to retrieve username from ResultSet", e);
                } finally {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        Logger.warn("Failed to close ResultSet", e);
                    }
                }
            } else {
                game.userData.setHighScore("0");
            }
        }
    }

    public void saveHighestScore(UUID sessionID, int highestScore) {
        if (!isConnectionAlive()) {
            Logger.info("Connection to database is not alive, skipping saving highest score: %d", highestScore);
        } else {
            ResultSet resultSet = readFromDatabase(sessionID);
            if (resultSet != null) {
                try {
                    if (resultSet.next()) {
                        if (highestScore > Integer.parseInt(resultSet.getString("highest_scores"))) {
                            resultSet.updateString("highest_scores", String.valueOf(highestScore));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Getter
    private enum PongServerInfo {
        Database("jdbc:mariadb://localhost:3306/pong", "root", "123");

        final String dbUrl;
        final String dbUser;
        final String dbPassword;

        PongServerInfo(String dbUrl, String dbUser, String dbPassword) {
            this.dbUrl = dbUrl;
            this.dbUser = dbUser;
            this.dbPassword = dbPassword;
        }
    }
}
