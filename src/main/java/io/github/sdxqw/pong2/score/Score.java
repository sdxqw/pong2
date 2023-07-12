package io.github.sdxqw.pong2.score;

public class Score {
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
}
