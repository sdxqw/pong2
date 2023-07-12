package io.github.sdxqw.pong2.enitity;

import io.github.sdxqw.pong2.score.Score;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
public class Ball {
    private final float width;
    private final float height;
    private final Paddle player;
    private final Paddle player2;
    private final Random random;
    private final Score score;
    private float x;
    private float y;
    private float speed;
    private float directionX;
    private float directionY;
    private boolean hasBallSpawned;

    public Ball(float widthWindow, float heightWindow, Paddle player, Paddle player2, Score score) {
        this.x = widthWindow / 2;
        this.y = heightWindow / 2;
        this.width = 20;
        this.height = 20;
        this.speed = 150f;
        this.player = player;
        this.player2 = player2;
        this.random = new Random();
        this.score = score;
        this.hasBallSpawned = false;
    }

    public void moveBall(float windowHeight, float windowWidth, double deltaTime) {
        if (!hasBallSpawned) {
            spawnBall(windowWidth, windowHeight);
        } else {
            float newX = calculateNewX(deltaTime);
            float newY = calculateNewY(windowHeight, deltaTime);

            if (isOutOfBounds(newX, windowWidth)) {
                if (newX < 0) {
                    score.incrementPlayer2Score();
                } else {
                    score.incrementPlayer1Score();
                }
                spawnBall(windowWidth, windowHeight);
                return;
            }

            if (isCollidingWithPlayer(newX, newY) || isCollidingWithPlayer2(newX, newY)) {
                directionX *= -1;
            }

            x = newX;
            y = newY;
            incrementSpeed();
        }
    }

    public void resetSpeed() {
        speed = 150f;
    }

    public void incrementSpeed() {
        speed += 0.01f;
    }

    public void spawnBall(float windowWidth, float windowHeight) {
        x = windowWidth / 2 - width / 2;
        y = windowHeight / 2 - height / 2;
        directionX = getRandomDirection();
        directionY = getRandomDirection();
        hasBallSpawned = true;
    }

    private float calculateNewX(double deltaTime) {
        return (float) (x + speed * directionX * deltaTime);
    }

    private float calculateNewY(float windowHeight, double deltaTime) {
        float newY = (float) (y + speed * directionY * deltaTime);
        if (newY < 0 || newY + height > windowHeight) {
            directionY *= -1;
        }
        return newY;
    }

    private boolean isOutOfBounds(float newX, float windowWidth) {
        return newX < 0 || newX + width > windowWidth;
    }

    private boolean isCollidingWithPlayer(float newX, float newY) {
        return player.isColliding(newX, newY, width, height);
    }

    private boolean isCollidingWithPlayer2(float newX, float newY) {
        return player2.isColliding(newX, newY, width, height);
    }

    private float getRandomDirection() {
        return random.nextBoolean() ? 1 : -1;
    }
}
