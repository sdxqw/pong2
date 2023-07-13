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
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;

public class PauseState extends GameState {
    private final List<Button> buttons;
    private final PlayState playState;
    private Button resumeButton;
    private Button exitButton;
    private Button mainMenuButton;

    private boolean isPaused = false;

    public PauseState(PongGame game, PlayState playState) {
        super(game);
        this.playState = playState;
        buttons = new ArrayList<>();

        for (int i = 0; i <= WINDOW_HEIGHT; i += 25) {
            resumeButton = new Button(game, 0, (float) WINDOW_WIDTH / 2 - i + 45, (float) WINDOW_HEIGHT / 2 - 50, 200, 80, "Resume", 35);
            mainMenuButton = new Button(game, 1, (float) WINDOW_WIDTH / 2 - i + 30, (float) WINDOW_HEIGHT / 2 + 10, 200, 80, "Main Menu", 22);
            exitButton = new Button(game, 2, (float) WINDOW_WIDTH / 2 - i - 5, (float) WINDOW_HEIGHT / 2 + 60, 200, 80, "Exit", 18);
        }

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
        game.font.drawText("PAUSED", NVG_ALIGN_CENTER | NVG_ALIGN_BASELINE, (float) WINDOW_WIDTH / 2 - 525, (float) WINDOW_HEIGHT / 2 - 200, 50, Utils.color(255, 255, 255, 255));
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
