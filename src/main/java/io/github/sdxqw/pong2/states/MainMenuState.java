package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.rendering.Button;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static io.github.sdxqw.pong2.PongGame.WINDOW_HEIGHT;
import static io.github.sdxqw.pong2.PongGame.WINDOW_WIDTH;
import static org.lwjgl.nanovg.NanoVG.*;

public class MainMenuState extends GameState {
    private final List<Button> buttons;
    private final PlayState playState;

    private Button startButton;
    private Button keybindingList;
    private Button exitButton;
    private Button topListButton;

    public MainMenuState(PongGame game) {
        super(game);
        buttons = new ArrayList<>();
        playState = new PlayState(game);

        for (int i = 0; i <= WINDOW_HEIGHT; i += 15) {
            startButton = new Button(0, (float) WINDOW_WIDTH / 2 - i + 45, (float) WINDOW_HEIGHT / 2 - 50, 200, 80, "Start", 35);
            topListButton = new Button(1, (float) WINDOW_WIDTH / 2 - i + 45, (float) WINDOW_HEIGHT / 2 + 10, 200, 80, "Top-List", 22);
            keybindingList = new Button(2, (float) WINDOW_WIDTH / 2 - i + 75, (float) WINDOW_HEIGHT / 2 + 60, 200, 80, "Keybinding List", 20);
            exitButton = new Button(3, (float) WINDOW_WIDTH / 2 - i + 20, (float) WINDOW_HEIGHT / 2 + 110, 200, 80, "Exit", 18);
        }

        startButton.setOnActivated(this::startGame);
        topListButton.setOnActivated(() -> game.changeState(new TopListState(game)));
        keybindingList.setOnActivated(() -> game.changeState(new KeyListState(game)));
        exitButton.setOnActivated(game::endGame);

        buttons.add(startButton);
        buttons.add(topListButton);
        buttons.add(keybindingList);
        buttons.add(exitButton);
    }

    @Override
    public void render(Rendering renderer, long vg) {
        game.font.drawText("PONG 2", NVG_ALIGN_CENTER | NVG_ALIGN_BOTTOM, (float) WINDOW_WIDTH / 2 - 525, (float) WINDOW_HEIGHT / 2 - 190, 50, Utils.color(1f, 1f, 1f, 1f));
        game.font.drawText(game.userName, NVG_ALIGN_CENTER | NVG_ALIGN_LEFT, (float) WINDOW_WIDTH / 2 - 620, (float) WINDOW_HEIGHT / 2 - 170, 25, Utils.color(0.6f, 0.2f, 0.4f, 0.6f));
        buttons.forEach(e -> e.renderButton(game.vg, game.font));
    }

    @Override
    public void update(double deltaTime) {
    }

    public void onKeyPressed(int key, int action) {
        startButton.onKeyPressed(key, action, buttons);
        topListButton.onKeyPressed(key, action, buttons);
        keybindingList.onKeyPressed(key, action, buttons);
        exitButton.onKeyPressed(key, action, buttons);
    }

    private void startGame() {
        game.changeState(playState);
    }
}
