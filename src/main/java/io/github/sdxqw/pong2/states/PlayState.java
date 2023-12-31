package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.entity.Ball;
import io.github.sdxqw.pong2.entity.Paddle;
import io.github.sdxqw.pong2.modes.TypeModes;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.nanovg.NanoVG.*;

public class PlayState extends GameState {
    private static final int LINE_SPACING = 20;
    private static final int LINE_LENGTH = 10;
    public final Ball ball;
    private final Paddle player1;
    private final Paddle player2;
    private final GameModeState gameModeState;
    private double blinkTimer = 0;
    private float deltaTime = 0;
    private boolean isTextVisible = true;
    private boolean isGameTerminated = false;

    public PlayState(PongGame game, GameModeState gameModeState) {
        super(game);
        this.gameModeState = gameModeState;
        player1 = new Paddle(game);
        player2 = new Paddle(game);
        player2.getPosition().x = PongGame.WINDOW_WIDTH - player2.getWidth() - player2.getPosition().x;
        ball = new Ball(PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT, player1, player2, game.score);

        if (gameModeState.mode == TypeModes.EASY) {
            player1.setSpeed(gameModeState.mode.getSpeedPlayer());
            player2.setSpeed(gameModeState.mode.getSpeedPlayer());
            ball.setSpeed(gameModeState.mode.getSpeedBall());
            resetGame();
        }

        if (gameModeState.mode == TypeModes.NORMAL) {
            player1.setSpeed(gameModeState.mode.getSpeedPlayer());
            player2.setSpeed(gameModeState.mode.getSpeedPlayer());
            ball.setSpeed(gameModeState.mode.getSpeedBall());
            resetGame();
        }

        if (gameModeState.mode == TypeModes.HARD) {
            player1.setSpeed(gameModeState.mode.getSpeedPlayer());
            player2.setSpeed(gameModeState.mode.getSpeedPlayer());
            ball.setSpeed(gameModeState.mode.getSpeedBall());
            resetGame();
        }

        if (gameModeState.mode == TypeModes.ULTIMATE) {
            player1.setSpeed(gameModeState.mode.getSpeedPlayer());
            player2.setSpeed(gameModeState.mode.getSpeedPlayer());
            ball.setSpeed(gameModeState.mode.getSpeedBall());
            resetGame();
        }
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
    public void update(float deltaTime) {
        if (game.inputManager.isKeyPressed(game.keyListState.getValueByIndex(10)))
            isGameTerminated = true;

        if (!isGameTerminated) {
            ball.moveBall(PongGame.WINDOW_HEIGHT, PongGame.WINDOW_WIDTH, deltaTime);
            player1.movePaddle(PongGame.WINDOW_HEIGHT, deltaTime);
            player2.moveBotPaddle(PongGame.WINDOW_HEIGHT, deltaTime, ball);
            game.score.player1Scores.add(game.score.getPlayer1Score());
        }

        this.deltaTime = deltaTime;
    }

    public void drawWin() {
        if (game.score.getPlayer1Score() == gameModeState.getMode().getMaxScore() || game.score.getBotScore() == gameModeState.getMode().getMaxScore()) {
            isGameTerminated = true;
            game.score.player1Scores.add(game.score.getPlayer1Score());
        }

        if (isGameTerminated) {
            nvgBeginPath(game.vg);
            nvgRect(game.vg, 0, 0, PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT);
            nvgFillColor(game.vg, Utils.color(0f, 0f, 0f, 1f));
            nvgFill(game.vg);

            if (game.score.getPlayer1Score() == game.score.getBotScore())
                game.font.drawText("None Won!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 100,
                        100, Utils.color(1f, 1f, 1f, 1f));
            else if (game.score.getPlayer1Score() > game.score.getBotScore())
                game.font.drawText(game.userData.getUserName() + " Won!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 100,
                        100, Utils.color(1f, 1f, 1f, 1f));
            else
                game.font.drawText("Bot Won!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 100,
                        100, Utils.color(1f, 1f, 1f, 1f));

            game.font.drawText(String.valueOf(game.score.getPlayer1Score()), NVG_ALIGN_RIGHT,
                    (float) PongGame.WINDOW_WIDTH / 2 - 120, (float) PongGame.WINDOW_HEIGHT / 2 + 35, 60,
                    Utils.color(1f, 1f, 1f, 1f));

            game.font.drawText(String.valueOf(game.score.getBotScore()), NVG_ALIGN_LEFT,
                    (float) PongGame.WINDOW_WIDTH / 2 + 120, (float) PongGame.WINDOW_HEIGHT / 2 + 35, 60,
                    Utils.color(1f, 1f, 1f, 1f));

            game.font.drawText("-", NVG_ALIGN_LEFT,
                    (float) PongGame.WINDOW_WIDTH / 2 - 10, (float) PongGame.WINDOW_HEIGHT / 2 + 25, 60,
                    Utils.color(1f, 1f, 1f, 1f));

            blinkTimer += deltaTime;
            if (blinkTimer >= 0.3f) {
                isTextVisible = !isTextVisible;
                blinkTimer = 0;
            }

            if (isTextVisible) {
                game.font.drawText("Press SPACE to go back", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 + 320, 22,
                        Utils.color(0.8f, 0.8f, 0.8f, 0.8f));
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
        game.font.drawText(String.valueOf(game.score.getBotScore()), NVG_ALIGN_LEFT,
                (float) PongGame.WINDOW_WIDTH / 2 + 25, 55, 45,
                Utils.color(1f, 1f, 1f, 1f));
    }

    private void resetGame() {
        isGameTerminated = false;
        game.isGamePaused = false;
        game.score.resetScore();
        ball.resetSpeed(game);
        ball.spawnBall(PongGame.WINDOW_WIDTH, PongGame.WINDOW_HEIGHT);
        player1.resetPlayerPosition();
        player2.resetBotPosition();
        game.changeState(new GameModeState(game));
    }
}
