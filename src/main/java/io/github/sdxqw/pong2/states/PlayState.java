package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.entity.Ball;
import io.github.sdxqw.pong2.entity.Paddle;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.nanovg.NanoVG.*;

public class PlayState extends GameState {
    private static final int WINNING_SCORE = 5;
    private static final int LINE_SPACING = 20;
    private static final int LINE_LENGTH = 10;
    public final Ball ball;
    private final Paddle player1;
    private final Paddle player2;
    private double blinkTimer = 0;
    private boolean isTextVisible = true;
    private float deltaTime;
    private boolean isGameTerminated;


    public PlayState(PongGame game) {
        super(game);
        player1 = new Paddle(game.inputManager);
        player2 = new Paddle(game.inputManager);
        player2.getPosition().x = PongGame.WINDOW_WIDTH - player2.getWidth() - player2.getPosition().x;
        ball = new Ball(PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT, player1, player2, game.score);
        isGameTerminated = false;
    }

    @Override
    public void render(Rendering renderer, long vg) {
        drawWin();

        if (!isGameTerminated) {
            renderer.renderPaddle(vg, player1);
            renderer.renderPaddle(vg, player2);
            renderer.renderBall(vg, ball);

            for (int i = 0; i < PongGame.WINDOW_HEIGHT; i += LINE_SPACING) {
                renderer.renderLine(vg, (float) PongGame.WINDOW_WIDTH / 2, i,
                        (float) PongGame.WINDOW_WIDTH / 2, i + LINE_LENGTH);
            }

            renderScores();
        }
    }

    @Override
    public void update(double deltaTime) {
        if (game.inputManager.isKeyPressed(game.keyListState.getValueByIndex(5)))
            isGameTerminated = true;

        if (!isGameTerminated) {
            ball.moveBall(PongGame.WINDOW_HEIGHT, PongGame.WINDOW_WIDTH, deltaTime);
            player1.movePaddle(PongGame.WINDOW_HEIGHT, deltaTime);
            player2.moveBotPaddle(PongGame.WINDOW_HEIGHT, deltaTime, ball);
            game.score.scores.add(game.score.getPlayer1Score());
        }

        this.deltaTime = (float) deltaTime;
    }

    public void drawWin() {
        if (game.score.getPlayer1Score() == WINNING_SCORE || game.score.getPlayer2Score() == WINNING_SCORE) {
            isGameTerminated = true;
            game.score.scores.add(game.score.getPlayer1Score());
        }

        if (isGameTerminated) {
            nvgBeginPath(game.vg);
            nvgRect(game.vg, 0, 0, PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT);
            nvgFillColor(game.vg, Utils.color(0f, 0f, 0f, 1f));
            nvgFill(game.vg);

            game.showPauseMenu = false;

            if (game.score.getPlayer1Score() == game.score.getPlayer2Score())
                game.font.drawText("None Won!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 100,
                        100, Utils.color(1f, 1f, 1f, 1f));
            else if (game.score.getPlayer1Score() > game.score.getPlayer2Score())
                game.font.drawText(game.userName + " Won!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 100,
                        100, Utils.color(1f, 1f, 1f, 1f));
            else
                game.font.drawText("Bot Won!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 100,
                        100, Utils.color(1f, 1f, 1f, 1f));

            game.font.drawText(String.valueOf(game.score.getPlayer1Score()), NVG_ALIGN_RIGHT,
                    (float) PongGame.WINDOW_WIDTH / 2 - 120, (float) PongGame.WINDOW_HEIGHT / 2 + 35, 60,
                    Utils.color(1f, 1f, 1f, 1f));

            game.font.drawText(String.valueOf(game.score.getPlayer2Score()), NVG_ALIGN_LEFT,
                    (float) PongGame.WINDOW_WIDTH / 2 + 120, (float) PongGame.WINDOW_HEIGHT / 2 + 35, 60,
                    Utils.color(1f, 1f, 1f, 1f));

            game.font.drawText("-", NVG_ALIGN_LEFT,
                    (float) PongGame.WINDOW_WIDTH / 2 - 10, (float) PongGame.WINDOW_HEIGHT / 2 + 25, 60,
                    Utils.color(1f, 1f, 1f, 1f));

            blinkTimer += deltaTime;
            if (blinkTimer >= 0.25f) {
                isTextVisible = !isTextVisible;
                blinkTimer = 0;
            }

            if (isTextVisible) {
                game.font.drawText("Press SPACE to restart", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 + 100, 25,
                        Utils.color(1f, 1f, 1f, 1f));
            }
            if (game.inputManager.isKeyPressed(GLFW_KEY_SPACE)) {
                resetGame();
            }
        }
    }

    private void renderScores() {
        game.font.drawText(String.valueOf(game.score.getPlayer1Score()), NVG_ALIGN_RIGHT,
                (float) PongGame.WINDOW_WIDTH / 2 - 25, 55, 45,
                Utils.color(1f, 1f, 1f, 1f));
        game.font.drawText(String.valueOf(game.score.getPlayer2Score()), NVG_ALIGN_LEFT,
                (float) PongGame.WINDOW_WIDTH / 2 + 25, 55, 45,
                Utils.color(1f, 1f, 1f, 1f));
    }

    private void resetGame() {
        isGameTerminated = false;
        game.showPauseMenu = true;
        game.isGamePaused = false;
        game.score.resetScore();
        ball.resetSpeed();
        ball.spawnBall(PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT);
        player1.resetPlayerPosition();
        player2.resetBotPosition();
    }
}
