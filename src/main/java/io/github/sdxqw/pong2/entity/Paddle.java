package io.github.sdxqw.pong2.entity;

import io.github.sdxqw.pong2.input.InputManager;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

@Getter
public class Paddle {
    private final float width;
    private final float height;
    private final float speed;
    private final InputManager inputManager;
    @Setter
    private Vector2f position;

    public Paddle(long window) {
        this.position = new Vector2f(20, 50);
        this.width = 20;
        this.height = 150;
        this.speed = 350f;
        this.inputManager = new InputManager(window);
    }

    public void movePaddle(float windowHeight, double deltaTime) {
        if (inputManager.isKeyPressed(GLFW.GLFW_KEY_W) && position.y > 0) {
            position.y -= speed * deltaTime;
        }
        if (inputManager.isKeyPressed(GLFW.GLFW_KEY_S) && position.y + height < windowHeight) {
            position.y += speed * deltaTime;
        }
    }

    public void moveBotPaddle(float windowHeight, double deltaTime, Ball ball) {
        float paddleCenterY = position.y + height / 2;
        float ballCenterY = ball.getPosition().y + ball.getHeight() / 2;
        float errorOffset = (float) (Math.random() * 100) - 50;
        ballCenterY += errorOffset;
        float maxMovementSpeed = (float) (speed * deltaTime * 0.7f);

        if (paddleCenterY < ballCenterY &&  position.y + height + maxMovementSpeed < windowHeight) {
            position.y += Math.min(maxMovementSpeed, ballCenterY - paddleCenterY);
        } else if (paddleCenterY > ballCenterY &&  position.y - maxMovementSpeed > 0) {
            position.y -= Math.min(maxMovementSpeed, paddleCenterY - ballCenterY);
        }
    }

    public boolean isColliding(float ballX, float ballY, float ballWidth, float ballHeight) {
        float ballRight = ballX + ballWidth;
        float ballBottom = ballY + ballHeight;
        float paddleRight = position.x + width;
        float paddleBottom = position.y + height;

        return  position.x < ballRight && paddleRight > ballX && position.y < ballBottom && paddleBottom > ballY;
    }
}
