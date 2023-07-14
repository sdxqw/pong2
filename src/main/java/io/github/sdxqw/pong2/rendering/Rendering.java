package io.github.sdxqw.pong2.rendering;

import io.github.sdxqw.pong2.entity.Ball;
import io.github.sdxqw.pong2.entity.Paddle;
import io.github.sdxqw.pong2.utils.Utils;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;


public class Rendering {

    private static final NVGColor PAUSE_MENU_COLOR;

    static {
        PAUSE_MENU_COLOR = NVGColor.create();
        PAUSE_MENU_COLOR.r(0.12f);
        PAUSE_MENU_COLOR.g(0.12f);
        PAUSE_MENU_COLOR.b(0.12f);
        PAUSE_MENU_COLOR.a(0.86f);
    }

    public void renderPauseMenu(long vg, float screenWidth, float screenHeight) {
        NVGColor color = NVGColor.create();
        color.r(0.18f);
        color.g(0.18f);
        color.b(0.18f);
        color.a(0.66f);

        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRect(vg, 0, 0, screenWidth, screenHeight);
        NanoVG.nvgFillColor(vg, color);
        NanoVG.nvgFill(vg);
    }

    public void renderLine(long vg, float x1, float y1, float x2, float y2) {
        drawLine(vg, x1, y1, x2, y2, 2f, Utils.color(1f, 1f, 1f, 1f));
    }

    public void drawLine(long vg, float x1, float y1, float x2, float y2, float width, NVGColor color) {
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, x1, y1);
        NanoVG.nvgLineTo(vg, x2, y2);
        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, width);
        NanoVG.nvgStroke(vg);
    }

    public void renderPaddle(long vg, Paddle paddle) {
        drawEntity(vg, paddle.getPosition(), paddle.getWidth(), paddle.getHeight());
    }

    public void renderBall(long vg, Ball ball) {
        drawEntity(vg, ball.getPosition(), ball.getWidth(), ball.getHeight());
    }

    public void drawRoundedRect(long vg, float x, float y, float width, float height, float radius) {
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
        NanoVG.nvgFillColor(vg, Utils.color(0.2f, 0.2f, 0.2f, 1.0f));
    }

    private void drawEntity(long vg, Vector2f position, float width, float height) {
        NVGColor color = NVGColor.create();
        color.r(1f);
        color.g(1f);
        color.b(1f);
        color.a(1f);

        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRect(vg, position.x, position.y, width, height);
        NanoVG.nvgFillColor(vg, color);
        NanoVG.nvgFill(vg);
    }

}