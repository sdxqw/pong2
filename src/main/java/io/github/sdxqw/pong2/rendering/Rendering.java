package io.github.sdxqw.pong2.rendering;

import io.github.sdxqw.pong2.enitity.Ball;
import io.github.sdxqw.pong2.enitity.Paddle;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;


public class Rendering {

    private static final NVGColor PAUSE_MENU_COLOR;

    static {
        PAUSE_MENU_COLOR = NVGColor.create();
        PAUSE_MENU_COLOR.r(0.18f);
        PAUSE_MENU_COLOR.g(0.18f);
        PAUSE_MENU_COLOR.b(0.18f);
        PAUSE_MENU_COLOR.a(0.66f);
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

    public void renderPaddle(long vg, Paddle paddle) {
        drawEntity(vg, paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
    }

    public void renderBall(long vg, Ball ball) {
        drawEntity(vg, ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight());
    }

    private void drawEntity(long vg, float x, float y, float width, float height) {
        NVGColor color = NVGColor.create();
        color.r(1f);
        color.g(1f);
        color.b(1f);
        color.a(1f);

        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRect(vg, x, y, width, height);
        NanoVG.nvgFillColor(vg, color);
        NanoVG.nvgFill(vg);
    }

}