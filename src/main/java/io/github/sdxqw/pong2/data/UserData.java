package io.github.sdxqw.pong2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.sdxqw.logger.Logger;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Getter
@Setter
public class UserData {
    private static final String APP_DIR = System.getProperty("user.home") + File.separator + "pong2-dir";
    private static final String SESSION_FILE_PATH = APP_DIR + File.separator + "session.json";

    private UUID sessionID;

    private String userName;
    private String highScore;

    public void loadSessionID() {
        try {
            File sessionFile = new File(SESSION_FILE_PATH);
            if (sessionFile.exists()) {
                Gson gson = new Gson();
                FileReader reader = new FileReader(sessionFile);
                UserData userData = gson.fromJson(reader, UserData.class);
                this.sessionID = userData.getSessionID();
                reader.close();
                Logger.info("Session ID loaded");
            } else {
                createNewSessionID();
                saveSessionID();
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
            Logger.info("Session ID saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAppDirIfNotExists() throws IOException {
        Path appDirPath = Paths.get(APP_DIR);
        if (!Files.exists(appDirPath)) {
            Logger.info("Creating app directory");
            Files.createDirectories(appDirPath);
        }
    }

    private void createNewSessionID() {
        Logger.info("Creating new session ID");
        sessionID = UUID.randomUUID();
    }
}
