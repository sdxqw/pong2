package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.rendering.Button;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static io.github.sdxqw.pong2.PongGame.WINDOW_HEIGHT;
import static io.github.sdxqw.pong2.PongGame.WINDOW_WIDTH;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;

public class MainMenuState extends GameState {
    private final List<Button> buttons;
    private final PlayState playState;

    private final Button startButton;
    private final Button exitButton;

    private final Font font;

    public MainMenuState(PongGame game) {
        super(game);
        font = new Font(game.vg, "pixel");
        buttons = new ArrayList<>();
        playState = new PlayState(game);
        startButton = new Button(0, (float) WINDOW_WIDTH / 2, (float) WINDOW_HEIGHT / 2 - 50, 200, 80, "Start", 30);
        exitButton = new Button(1, (float) WINDOW_WIDTH / 2, (float) WINDOW_HEIGHT / 2 + 60, 200, 80, "Exit", 18);
        startButton.setOnActivated(this::startGame);
        exitButton.setOnActivated(game::endGame);
        buttons.add(startButton);
        buttons.add(exitButton);
    }

    @Override
    public void render(Rendering renderer, long vg) {
        font.drawText("PONG 2", NVG_ALIGN_BASELINE, (float) WINDOW_HEIGHT / 2 - 150, 50, 22, Utils.color(255, 255, 255, 255));
        buttons.forEach(e -> e.renderButton(game.vg, game.font));
    }

    @Override
    public void update(double deltaTime) {

    }

    public void onKeyPressed(int key, int action) {
        startButton.onKeyPressed(key, action, buttons);
        exitButton.onKeyPressed(key, action, buttons);
    }

    private void startGame() {
        game.changeState(playState);
    }
}
