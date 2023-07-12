package io.github.sdxqw.pong2.entity;

import io.github.sdxqw.pong2.score.Score;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

import java.util.Random;

@Getter
@Setter
public class Ball {
    private final float width;
    private final float height;
    private final Paddle player;
    private final Paddle player2;
    private final Score score;
    private float speed;
    private boolean hasBallSpawned;

    private Vector2f position;
    private Vector2f direction;
    private Random random;

    public Ball(float widthWindow, float heightWindow, Paddle player, Paddle player2, Score score) {
        this.position = new Vector2f(widthWindow / 2, heightWindow / 2);
        this.direction = new Vector2f(getRandomDirection(), getRandomDirection());
        this.width = 20;
        this.height = 20;
        this.speed = 150f;
        this.player = player;
        this.player2 = player2;
        this.score = score;
        this.hasBallSpawned = false;
        this.random = new Random();
    }

    public void moveBall(float windowHeight, float windowWidth, double deltaTime) {
        if (!hasBallSpawned) {
            spawnBall(windowWidth, windowHeight);
        } else {
            Vector2f newPosition = calculateNewPosition(deltaTime);

            if (isOutOfBounds(newPosition.x, windowWidth)) {
                if (newPosition.x < 0) {
                    score.incrementPlayer2Score();
                } else {
                    score.incrementPlayer1Score();
                }
                spawnBall(windowWidth, windowHeight);
                return;
            }

            // Check collision with top and bottom edges
            if (newPosition.y < 0 || newPosition.y + height > windowHeight) {
                direction.y *= -1; // Reflect the y-direction
            }

            if (isCollidingWithPlayer(newPosition) || isCollidingWithPlayer2(newPosition)) {
                direction.x *= -1;
            }

            position = newPosition;
            incrementSpeed();
        }
    }

    public void resetSpeed() {
        speed = 150f;
    }

    public void incrementSpeed() {
        speed += 0.01f;
    }

    private Vector2f calculateNewPosition(double deltaTime) {
        float newX = (float) (position.x + speed * direction.x * deltaTime);
        float newY = (float) (position.y + speed * direction.y * deltaTime);
        return new Vector2f(newX, newY);
    }

    public void spawnBall(float windowWidth, float windowHeight) {
        position.x = windowWidth / 2 - width / 2;
        position.y = windowHeight / 2 - height / 2;
        direction.x = getRandomDirection();
        direction.y = getRandomDirection();
        hasBallSpawned = true;
    }

    private boolean isOutOfBounds(float newX, float windowWidth) {
        return newX < 0 || newX + width > windowWidth;
    }

    private boolean isCollidingWithPlayer(Vector2f newPosition) {
        return player.isColliding(newPosition.x, newPosition.y, width, height);
    }

    private boolean isCollidingWithPlayer2(Vector2f newPosition) {
        return player2.isColliding(newPosition.x, newPosition.y, width, height);
    }

    private float getRandomDirection() {
        if (random != null)
            return random.nextBoolean() ? 1 : -1;
        return 1;
    }
}