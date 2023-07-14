package io.github.sdxqw.pong2.score;

import java.util.ArrayList;
import java.util.List;

public class Score {
    public final List<Integer> player1Scores = new ArrayList<>();
    private int player1Score;
    private int botScore;

    public Score() {
        player1Score = 0;
        botScore = 0;
    }

    public void incrementPlayer1Score() {
        player1Score++;
        player1Scores.add(player1Score);
    }

    public void incrementBotScore() {
        botScore++;
        player1Scores.add(botScore);
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getBotScore() {
        return botScore;
    }

    public void resetScore() {
        player1Score = 0;
        botScore = 0;
    }

    public int getHighestPlayer1Score() {
        if (player1Scores.isEmpty()) {
            return 0;
        }
        return player1Scores.stream().max(Integer::compareTo).orElse(0);
    }
}
