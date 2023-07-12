package io.github.sdxqw.pong2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class UserData {
    private static final String SESSION_FILE_PATH = "session.json";
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSessionID() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(SESSION_FILE_PATH);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }
}
