package io.github.sdxqw.pong2.modes;

import lombok.Getter;

@Getter
public enum TypeModes {

    EASY("easy", 450f, 350f, 5),
    NORMAL("normal", 850f, 500f, 10),
    HARD("hard", 1060f, 720f, 20),
    ULTIMATE("ultimate", 1100f, 820f, 900);

    private final String name;
    private final float speedBall;
    private final float speedPlayer;
    private final int maxScore;

    TypeModes(String name, float speedBall, float speedPlayer, int maxScore) {
        this.name = name;
        this.speedBall = speedBall;
        this.speedPlayer = speedPlayer;
        this.maxScore = maxScore;
    }
}
