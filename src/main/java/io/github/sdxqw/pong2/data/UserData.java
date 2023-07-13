package io.github.sdxqw.pong2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class UserData {
    private static final String APP_DIR = System.getProperty("user.home") + File.separator + "pong2-dir";
    private static final String SESSION_FILE_PATH = APP_DIR + File.separator + "session.json";
    private UUID sessionID;

    public void loadSessionID() {
        try {
            File sessionFile = new File(SESSION_FILE_PATH);
            if (sessionFile.exists()) {
                Gson gson = new Gson();
                FileReader reader = new FileReader(sessionFile);
                UserData userData = gson.fromJson(reader, UserData.class);
                this.sessionID = userData.getSessionID();
                reader.close();
            } else {
                createNewSessionID(); // Create a new session ID if the file doesn't exist
                saveSessionID(); // Save the new session ID to the JSON file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSessionID() {
        try {
            createAppDirIfNotExists();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(SESSION_FILE_PATH);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAppDirIfNotExists() throws IOException {
        Path appDirPath = Paths.get(APP_DIR);
        if (!Files.exists(appDirPath)) {
            Files.createDirectories(appDirPath);
        }
    }

    private void createNewSessionID() {
        sessionID = UUID.randomUUID();
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }
}
