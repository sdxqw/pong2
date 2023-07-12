package io.github.sdxqw.pong2.enitity;

import io.github.sdxqw.pong2.input.InputManager;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

@Getter
public class Paddle {
    private final float width;
    private final float height;
    private final float speed;
    private final InputManager inputManager;
    @Setter
    private float x;
    private float y;

    public Paddle(long window) {
        this.x = 20;
        this.y = 50;
        this.width = 20;
        this.height = 150;
        this.speed = 350f;
        this.inputManager = new InputManager(window);
    }

    public void movePaddle(float windowHeight, double deltaTime) {
        if (inputManager.isKeyPressed(GLFW.GLFW_KEY_W) && y > 0) {
            y -= speed * deltaTime;
        }
        if (inputManager.isKeyPressed(GLFW.GLFW_KEY_S) && y + height < windowHeight) {
            y += speed * deltaTime;
        }
    }

    public void moveBotPaddle(float windowHeight, double deltaTime, Ball ball) {
        float paddleCenterY = y + height / 2;
        float ballCenterY = ball.getY() + ball.getHeight() / 2;
        float errorOffset = (float) (Math.random() * 100) - 50;
        ballCenterY += errorOffset;
        float maxMovementSpeed = (float) (speed * deltaTime * 0.7f);

        if (paddleCenterY < ballCenterY && y + height + maxMovementSpeed < windowHeight) {
            y += Math.min(maxMovementSpeed, ballCenterY - paddleCenterY);
        } else if (paddleCenterY > ballCenterY && y - maxMovementSpeed > 0) {
            y -= Math.min(maxMovementSpeed, paddleCenterY - ballCenterY);
        }
    }

    public boolean isColliding(float ballX, float ballY, float ballWidth, float ballHeight) {
        float ballRight = ballX + ballWidth;
        float ballBottom = ballY + ballHeight;
        float paddleRight = x + width;
        float paddleBottom = y + height;

        return x < ballRight && paddleRight > ballX && y < ballBottom && paddleBottom > ballY;
    }
}
