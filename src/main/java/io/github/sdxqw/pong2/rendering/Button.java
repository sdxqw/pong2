package io.github.sdxqw.pong2.rendering;

import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.nanovg.NanoVG;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;

public class Button {
    public float x;
    public float y;

    public int fontSize;
    public int id;
    public float width;
    public float height;
    public String text;
    private int selectedButtonIndex;
    private Runnable onActivated;

    public Button(int id, float x, float y, float width, float height, String text, int fontSize) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.fontSize = fontSize;
        this.selectedButtonIndex = 0;
    }

    public void renderButton(long vg, Font fontManager) {
        float x0 = x;
        float y0 = y;
        float textX = x + width / 2 - fontManager.measureTextWidth(text, fontSize) / 2;
        float textY = y + height / 2 + (fontSize >> 1) + 5;

        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x0, y0, width, height, 4f);
        NanoVG.nvgFillColor(vg, Utils.color(0.2f, 0.2f, 0.2f, 1.0f));

        if (selectedButtonIndex == id) {
            float lineY = textY + (fontSize >> 1) + 2;
            float lineWidth = fontManager.measureTextWidth(text, fontSize);

            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgMoveTo(vg, textX, lineY);
            NanoVG.nvgLineTo(vg, textX + lineWidth, lineY);
            NanoVG.nvgStrokeColor(vg, Utils.color(0.4f, 0.4f, 0.4f, 1.0f));
            NanoVG.nvgStrokeWidth(vg, 5.0f);
            NanoVG.nvgStroke(vg);

            fontManager.drawText(text, NVG_ALIGN_BOTTOM, textX, textY, fontSize, Utils.color(1.0f, 1.0f, 1.0f, 1.0f));
        } else {
            fontManager.drawText(text, NVG_ALIGN_BASELINE, textX, textY, fontSize, Utils.color(0.6f, 0.6f, 0.6f, 1.0f));
        }
    }

    public void setOnActivated(Runnable onActivated) {
        this.onActivated = onActivated;
    }

    public void onKeyPressed(int key, int action, List<Button> buttons) {
        boolean keyPressed = action == GLFW_PRESS;

        if (keyPressed && key == GLFW_KEY_W && selectedButtonIndex > 0) {
            selectedButtonIndex--;
        } else if (keyPressed && key == GLFW_KEY_S && selectedButtonIndex < buttons.size() - 1) {
            selectedButtonIndex++;
        } else if (keyPressed && (key == GLFW_KEY_ENTER || key == GLFW_KEY_D) && onActivated != null) {
            buttons.get(selectedButtonIndex).onActivated.run();
        }
    }

}