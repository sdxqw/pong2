package io.github.sdxqw.pong2.rendering;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.utils.Utils;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

public class Button {
    private final PongGame game;
    public float x;
    public float y;
    public float width;
    public float height;
    public int selectedButtonIndex;
    public int id;
    public int fontSize;
    public String text;
    private Runnable onActivated;

    public Button(PongGame game, int id, float x, float y, float width, float height, String text, int fontSize) {
        this.game = game;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontSize = fontSize;
        this.selectedButtonIndex = 0;
    }

    public void renderButton(long vg, Font fontManager, Rendering rendering) {
        float x0 = x - width / 2;
        float y0 = y;
        float textWidth = fontManager.measureTextWidth(text, fontSize);
        float textX = x - textWidth / 2 + width / 2;
        float textY = y + height / 2 + (fontSize >> 1) + 5;

        rendering.drawRoundedRect(vg, x0, y0, width, height, 4f);

        if (selectedButtonIndex == id) {
            fontManager.drawText("> " + text, NVG_ALIGN_BOTTOM | NVG_ALIGN_MIDDLE, textX, textY, fontSize, Utils.color(1.0f, 1.0f, 1.0f, 1.0f));
        } else {
            fontManager.drawText(text, NVG_ALIGN_BOTTOM | NVG_ALIGN_MIDDLE, textX, textY, fontSize, Utils.color(0.6f, 0.6f, 0.6f, 1.0f));
        }

    }

    public void setOnActivated(Runnable onActivated) {
        this.onActivated = onActivated;
    }

    public void onKeyPressed(int key, int action, List<Button> buttons) {
        boolean keyPressed = action == GLFW_PRESS;

        if (keyPressed && key == game.keyListState.getValueByIndex(4) && selectedButtonIndex > 0) {
            selectedButtonIndex--;
        } else if (keyPressed && key == game.keyListState.getValueByIndex(5) && selectedButtonIndex < buttons.size() - 1) {
            selectedButtonIndex++;
        } else if (keyPressed && (key == game.keyListState.getValueByIndex(7) || key == game.keyListState.getValueByIndex(6)) && onActivated != null) {
            buttons.get(selectedButtonIndex).onActivated.run();
        }
    }

}
