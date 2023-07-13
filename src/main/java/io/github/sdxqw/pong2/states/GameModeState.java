package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.modes.TypeModes;
import io.github.sdxqw.pong2.rendering.Button;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;
import lombok.Getter;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;

import static io.github.sdxqw.pong2.PongGame.WINDOW_HEIGHT;
import static io.github.sdxqw.pong2.PongGame.WINDOW_WIDTH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.nanovg.NanoVG.*;

public class GameModeState extends GameState {

    private final List<Button> buttons;
    @Getter
    public TypeModes mode;
    public boolean firstClick = true;
    private ButtonMode easyButton;
    private ButtonMode normalButton;
    private ButtonMode hardButton;
    private ButtonMode ultimateButton;

    private double blinkTimer = 0;
    private boolean isTextVisible = true;
    private float deltaTime;

    public GameModeState(PongGame game) {
        super(game);
        buttons = new ArrayList<>();
        mode = TypeModes.EASY;

        for (int i = 0; i <= WINDOW_HEIGHT; i += 15) {
            easyButton = new ButtonMode(game, 0, (float) WINDOW_WIDTH / 2 - i + 48, (float) PongGame.WINDOW_HEIGHT / 2 - 80, 200, 50, "Easy", 30, Utils.color(0.0f, 1f, 0.0f, 1f), Utils.color(0.0f, 0.5f, 0.0f, 1f));
            normalButton = new ButtonMode(game, 1, (float) WINDOW_WIDTH / 2 - i + 64, (float) PongGame.WINDOW_HEIGHT / 2 - 20, 200, 50, "Normal", 30, Utils.color(1f, 1f, 0.0f, 1f), Utils.color(0.5f, 0.5f, 0.0f, 1f));
            hardButton = new ButtonMode(game, 2, (float) WINDOW_WIDTH / 2 - i + 46, (float) PongGame.WINDOW_HEIGHT / 2 + 40, 200, 50, "Hard", 30, Utils.color(1f, 0.0f, 0.0f, 1f), Utils.color(0.5f, 0.0f, 0.0f, 1f));
            ultimateButton = new ButtonMode(game, 3, (float) WINDOW_WIDTH / 2 - i + 66, (float) PongGame.WINDOW_HEIGHT / 2 + 100, 200, 50, "Ultimate", 30, Utils.color(1f, 0.0f, 1f, 1f), Utils.color(0.5f, 0.0f, 0.5f, 1f));
        }

        easyButton.setOnActivated(() -> {
            setMode(TypeModes.EASY);
            game.changeState(new PlayState(game, this));
        });

        normalButton.setOnActivated(() -> {
            setMode(TypeModes.NORMAL);
            game.changeState(new PlayState(game, this));
        });

        hardButton.setOnActivated(() -> {
            setMode(TypeModes.HARD);
            game.changeState(new PlayState(game, this));
        });

        ultimateButton.setOnActivated(() -> {
            setMode(TypeModes.ULTIMATE);
            game.changeState(new PlayState(game, this));
        });

        buttons.add(easyButton);
        buttons.add(normalButton);
        buttons.add(hardButton);
        buttons.add(ultimateButton);
    }

    public void setMode(TypeModes mode) {
        switch (mode) {
            case EASY -> this.mode = TypeModes.EASY;
            case NORMAL -> this.mode = TypeModes.NORMAL;
            case HARD -> this.mode = TypeModes.HARD;
            case ULTIMATE -> this.mode = TypeModes.ULTIMATE;
            default -> {
            }
        }
    }

    @Override
    public void render(Rendering renderer, long vg) {
        game.font.drawText("Choose game mode", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 - 280, 40, Utils.color(1f, 1f, 1f, 1f));

        float infoX = (float) PongGame.WINDOW_WIDTH / 2;
        float infoY = (float) PongGame.WINDOW_HEIGHT / 2 - 24;
        float infoSpacing = 40;

        if (easyButton.selectedButtonIndex == 0) {
            setMode(TypeModes.EASY);
            game.font.drawText("Game Mode: " + mode.getName().toUpperCase(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY, 28, Utils.color(0.0f, 1f, 0.0f, 1f));
            game.font.drawText("Player Speed: " + (int) mode.getSpeedPlayer(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing, 28, Utils.color(0.0f, 1f, 0.0f, 1f));
            game.font.drawText("Ball Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 2, 28, Utils.color(0.0f, 1f, 0.0f, 1f));
            game.font.drawText("Max Scores: " + mode.getMaxScore(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 3, 28, Utils.color(0.0f, 1f, 0.0f, 1f));
        } else if (normalButton.selectedButtonIndex == 1) {
            setMode(TypeModes.NORMAL);
            game.font.drawText("Game Mode: " + mode.getName().toUpperCase(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY, 28, Utils.color(1f, 1f, 0.0f, 1f));
            game.font.drawText("Player Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing, 28, Utils.color(1f, 1f, 0.0f, 1f));
            game.font.drawText("Ball Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 2, 28, Utils.color(1f, 1f, 0.0f, 1f));
            game.font.drawText("Max Scores: " + mode.getMaxScore(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 3, 28, Utils.color(1f, 1f, 0.0f, 1f));
        } else if (hardButton.selectedButtonIndex == 2) {
            setMode(TypeModes.HARD);
            game.font.drawText("Game Mode: " + mode.getName().toUpperCase(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY, 28, Utils.color(1f, 0f, 0.0f, 1f));
            game.font.drawText("Player Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing, 28, Utils.color(1f, 0f, 0.0f, 1f));
            game.font.drawText("Ball Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 2, 28, Utils.color(1f, 0f, 0.0f, 1f));
            game.font.drawText("Max Scores: " + mode.getMaxScore(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 3, 28, Utils.color(1f, 0f, 0.0f, 1f));
        } else if (ultimateButton.selectedButtonIndex == 3) {
            setMode(TypeModes.ULTIMATE);
            game.font.drawText("Game Mode: " + mode.getName().toUpperCase(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY, 28, Utils.color(1f, 0.0f, 1.0f, 1f));
            game.font.drawText("Player Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing, 28, Utils.color(1f, 0.0f, 1.0f, 1f));
            game.font.drawText("Ball Speed: " + (int) mode.getSpeedBall(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 2, 28, Utils.color(1f, 0.0f, 1.0f, 1f));
            game.font.drawText("Max Scores: " + mode.getMaxScore(), NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, infoX, infoY + infoSpacing * 3, 28, Utils.color(1f, 0.0f, 1.0f, 1f));
        }

        blinkTimer += deltaTime;
        if (blinkTimer >= 0.25f) {
            isTextVisible = !isTextVisible;
            blinkTimer = 0;
        }

        if (isTextVisible)
            game.font.drawText("Press ESC to go back", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                    (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 + 320, 22,
                    Utils.color(0.8f, 0.8f, 0.8f, 0.8f));

        for (Button button : buttons) {
            button.renderButton(vg, game.font);
        }
    }


    public void onKeyPressed(int key, int action) {
        if (firstClick && key == GLFW_KEY_ENTER) {
            firstClick = false;
            return;
        }
        easyButton.onKeyPressed(key, action, buttons);
        normalButton.onKeyPressed(key, action, buttons);
        hardButton.onKeyPressed(key, action, buttons);
        ultimateButton.onKeyPressed(key, action, buttons);
    }

    @Override
    public void update(double deltaTime) {
        this.deltaTime = (float) deltaTime;
    }

    public static class ButtonMode extends Button {

        private final NVGColor color;
        private final NVGColor color2;

        public ButtonMode(PongGame game, int id, float x, float y, float width, float height, String text, int fontSize, NVGColor color, NVGColor color2) {
            super(game, id, x, y, width, height, text, fontSize, Utils.color(0.0f, 1f, 0.0f, 1f), Utils.color(0.0f, 0.5f, 0.0f, 1f));
            this.color = color;
            this.color2 = color2;

        }

        public void renderButton(long vg, Font fontManager) {
            float x0 = x - width / 2;
            float y0 = y;
            float textWidth = fontManager.measureTextWidth(text, fontSize);
            float textX = x - textWidth / 2 + width / 2;
            float textY = y + height / 2 + (fontSize >> 1) + 5;

            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRoundedRect(vg, x0, y0, width, height, 4f);
            NanoVG.nvgFillColor(vg, Utils.color(0.2f, 0.2f, 0.2f, 1.0f));

            if (selectedButtonIndex == id) {
                fontManager.drawText("> " + text, NVG_ALIGN_BOTTOM | NVG_ALIGN_MIDDLE, textX, textY, fontSize, color);
            } else {
                fontManager.drawText(text, NVG_ALIGN_BOTTOM | NVG_ALIGN_MIDDLE, textX, textY, fontSize, color2);
            }

        }

    }
}
