package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.rendering.Button;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static io.github.sdxqw.pong2.PongGame.WINDOW_HEIGHT;
import static io.github.sdxqw.pong2.PongGame.WINDOW_WIDTH;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;

public class PauseState extends GameState {
    public final List<Button> buttons;
    private final PlayState playState;
    private final Button resumeButton;
    private final Button exitButton;
    private final Button mainMenuButton;

    private boolean isPaused = false;

    public PauseState(PongGame game, PlayState playState) {
        super(game);
        this.playState = playState;
        buttons = new ArrayList<>();

        resumeButton = new Button(0, (float) WINDOW_WIDTH / 2, (float) WINDOW_HEIGHT / 2 - 50, 200, 80, "Resume", 25);
        mainMenuButton = new Button(1, (float) WINDOW_WIDTH / 2, (float) WINDOW_HEIGHT / 2 + 60, 200, 80, "Main Menu", 18);
        exitButton = new Button(2, (float) WINDOW_WIDTH / 2, (float) WINDOW_HEIGHT / 2 + 170, 200, 80, "Exit", 18);

        resumeButton.setOnActivated(this::resumeGame);
        mainMenuButton.setOnActivated(() -> game.changeState(new MainMenuState(game)));
        exitButton.setOnActivated(game::endGame);

        buttons.add(resumeButton);
        buttons.add(mainMenuButton);
        buttons.add(exitButton);
    }

    @Override
    public void render(Rendering renderer, long vg) {
        playState.render(renderer, vg);

        renderer.renderPauseMenu(game.vg, WINDOW_WIDTH, WINDOW_HEIGHT);
        game.font.drawText("PAUSED", NVG_ALIGN_BASELINE, (float) WINDOW_HEIGHT / 2 - 150, 50, 22, Utils.color(255, 255, 255, 255));
        buttons.forEach(e -> e.renderButton(game.vg, game.font));
    }

    @Override
    public void update(double deltaTime) {
        if (!isPaused) {
            playState.update(deltaTime);
        }
    }

    public void onKeyPressed(int key, int action) {
        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            isPaused = !isPaused;
            if (isPaused) {
                game.isGamePaused = true;
                game.changeState(this);
            } else {
                game.isGamePaused = false;
                game.changeState(playState);
            }
        }

        if (isPaused) {
            resumeButton.onKeyPressed(key, action, buttons);
            exitButton.onKeyPressed(key, action, buttons);
            mainMenuButton.onKeyPressed(key, action, buttons);
        }
    }

    private void resumeGame() {
        isPaused = false;
        game.isGamePaused = false;
        game.changeState(playState);
    }
}
