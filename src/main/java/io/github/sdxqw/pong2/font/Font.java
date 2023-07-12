package io.github.sdxqw.pong2.font;

import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGTextRow;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Font {
    private final long nvg;
    private final String fontName;

    public Font(long nvg, String fontName) {
        this.nvg = nvg;
        this.fontName = fontName;
        loadFont();
    }

    private void loadFont() {
        ByteBuffer fontPathData = null;
        ByteBuffer fontNameData = null;
        try {
            Path fontPath = Paths.get(Objects.requireNonNull(Utils.class.getResource("/textures/fonts/" + fontName + ".ttf")).toURI());
            byte[] fontNameBytes = (fontName + "\0").getBytes();
            fontNameData = BufferUtils.createByteBuffer(fontNameBytes.length);
            fontNameData.put(fontNameBytes);
            fontNameData.flip();

            byte[] fontPathBytes = (fontPath + "\0").getBytes();
            fontPathData = BufferUtils.createByteBuffer(fontPathBytes.length);
            fontPathData.put(fontPathBytes);
            fontPathData.flip();

            if (nvgCreateFont(nvg, fontNameData, fontPathData) == -1) {
                throw new IOException("Failed to load font: " + fontName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fontNameData != null)
                fontNameData.clear();

            if (fontPathData != null)
                fontPathData.clear();
        }
    }

    public void drawText(String text, int alignment, float x, float y, float fontSize, NVGColor color) {
        nvgFontSize(nvg, fontSize);
        nvgFontFace(nvg, fontName);
        nvgTextAlign(nvg, alignment);
        nvgFillColor(nvg, color);
        nvgText(nvg, x, y, text);
    }

    public float measureTextWidth(String text, float fontSize) {
        try (MemoryStack stack = stackPush()) {
            nvgFontFace(nvg, fontName);
            nvgFontSize(nvg, fontSize);

            NVGTextRow.Buffer textRows = NVGTextRow.calloc(1, stack);
            nvgTextBreakLines(nvg, text, Float.MAX_VALUE, textRows);

            return textRows.get(0).width();
        }
    }
}
