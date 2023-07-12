package io.github.sdxqw.pong2.score;

import java.util.ArrayList;
import java.util.List;

public class Score {
    public int highestScore;
    public List<Integer> scores = new ArrayList<>();
    private int player1Score;
    private int player2Score;

    public Score() {
        player1Score = 0;
        player2Score = 0;
    }

    public void incrementPlayer1Score() {
        player1Score++;
    }

    public void incrementPlayer2Score() {
        player2Score++;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void resetScore() {
        player1Score = 0;
        player2Score = 0;
    }

    // get the highest score from the list from teh player1
    public int getHighest() {
        if (scores.isEmpty()) {
            return 0;
        }
        int highest = scores.get(0);
        for (int score : scores) {
            if (score > highest) {
                highest = score;
            }
        }
        return highest;
    }

}
