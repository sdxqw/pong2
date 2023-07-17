package io.github.sdxqw.pong2.entity;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.modes.TypeModes;
import io.github.sdxqw.pong2.score.Score;
import io.github.sdxqw.pong2.states.GameModeState;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

import java.awt.*;
import java.util.Random;

@Getter
@Setter
public class Ball {
    private final float width;
    private final float height;
    private final Paddle player;
    private final Paddle bot;
    private final Score score;
    private float speed;
    private boolean hasBallSpawned;

    private Vector2f position;
    private Vector2f direction;
    private Random random;

    public Ball(float widthWindow, float heightWindow, Paddle player, Paddle bot, Score score) {
        this.position = new Vector2f(widthWindow / 2, heightWindow / 2);
        this.direction = new Vector2f(getRandomDirection(), getRandomDirection());
        this.width = 20;
        this.height = 20;
        this.speed = 500f;
        this.player = player;
        this.bot = bot;
        this.score = score;
        this.hasBallSpawned = false;
        this.random = new Random();
    }

    public void moveBall(float windowHeight, float windowWidth, float deltaTime) {
        if (!hasBallSpawned) {
            spawnBall(windowWidth, windowHeight);
        } else {
            Vector2f newPosition = calculateNewPosition(deltaTime);

            if (isOutOfBounds(newPosition.x, windowWidth)) {
                if (newPosition.x < 0) {
                    score.incrementBotScore();
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

            if (isCollidingWithPlayer(newPosition) || isCollidingWithBot(newPosition)) {
                direction.x *= -1;
                // Adjust ball position to prevent it from sticking inside the paddle
                if (isCollidingWithPlayer(newPosition)) {
                    float paddleRight = player.getPosition().x + player.getWidth();
                    if (newPosition.x < paddleRight - player.getWidth() / 2)
                        newPosition.x = player.getPosition().x - width;
                    else
                        newPosition.x = paddleRight;
                }
                if (isCollidingWithBot(newPosition)) {
                    float paddleLeft = bot.getPosition().x;
                    if (newPosition.x + width > paddleLeft + bot.getWidth() / 2)
                        newPosition.x = bot.getPosition().x + bot.getWidth();
                    else
                        newPosition.x = paddleLeft - width;
                }
            }

            position = newPosition;
        }
    }

    public void resetSpeed(PongGame game) {
        if (game.currentState instanceof GameModeState) {
            if (((GameModeState) game.currentState).getMode() == TypeModes.EASY) {
                speed = 450f;
            } else if (((GameModeState) game.currentState).getMode() == TypeModes.NORMAL) {
                speed = 850f;
            } else if (((GameModeState) game.currentState).getMode() == TypeModes.HARD) {
                speed = 1060f;
            } else if (((GameModeState) game.currentState).getMode() == TypeModes.ULTIMATE) {
                speed = 1100f;
            }
        }
    }

    public void incrementSpeed(float typeSpeed) {
        if (typeSpeed != 0)
            speed += typeSpeed;
        else speed += 0.1f;
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
        Rectangle ballRect = new Rectangle((int) newPosition.x, (int) newPosition.y, (int) width, (int) height);
        Rectangle playerRect = new Rectangle((int) player.getPosition().x, (int) player.getPosition().y, (int) player.getWidth(), (int) player.getHeight());
        return ballRect.intersects(playerRect);
    }

    private boolean isCollidingWithBot(Vector2f newPosition) {
        Rectangle ballRect = new Rectangle((int) newPosition.x, (int) newPosition.y, (int) width, (int) height);
        Rectangle botRect = new Rectangle((int) bot.getPosition().x, (int) bot.getPosition().y, (int) bot.getWidth(), (int) bot.getHeight());
        return ballRect.intersects(botRect);
    }

    private float getRandomDirection() {
        if (random != null)
            return random.nextBoolean() ? 1 : -1;
        return 1;
    }
}
