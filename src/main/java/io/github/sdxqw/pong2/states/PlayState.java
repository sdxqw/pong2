package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.enitity.Ball;
import io.github.sdxqw.pong2.enitity.Paddle;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;

import static org.lwjgl.nanovg.NanoVG.*;

public class PlayState extends GameState {
    public final Ball ball;
    private final Paddle player1;
    private final Paddle player2;

    public PlayState(PongGame game) {
        super(game);
        player1 = new Paddle(game.window);
        player2 = new Paddle(game.window);
        player2.setX(PongGame.WINDOW_WIDTH - player2.getWidth() - player2.getX());
        ball = new Ball(PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT, player1, player2, game.score);
    }

    @Override
    public void render(Rendering renderer, long vg) {
        renderer.renderPaddle(vg, player1);
        renderer.renderPaddle(vg, player2);
        renderer.renderBall(vg, ball);

        game.font.drawText("Player 1: " + game.score.getPlayer1Score(), NVG_ALIGN_LEFT, 25, 20, 22, Utils.color(1f, 1f, 1f, 1f));
        game.font.drawText("Player 2: " + game.score.getPlayer2Score(), NVG_ALIGN_RIGHT,25, 20, 22, Utils.color(1f, 1f, 1f, 1f));
    }

    @Override
    public void update(double deltaTime) {
        ball.moveBall(PongGame.WINDOW_HEIGHT, PongGame.WINDOW_WIDTH, deltaTime);
        player1.movePaddle(PongGame.WINDOW_HEIGHT, deltaTime);
        player2.moveBotPaddle(PongGame.WINDOW_HEIGHT, deltaTime, ball);
    }
}
