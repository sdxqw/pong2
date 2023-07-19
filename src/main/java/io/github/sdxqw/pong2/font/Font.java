package io.github.sdxqw.pong2.font;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGTextRow;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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

    public void loadFont() {
        String fontFilePath = "textures/fonts/" + fontName + ".ttf";
        try (InputStream inputStream = Font.class.getClassLoader().getResourceAsStream(fontFilePath)) {
            assert inputStream != null : "Error: Could not open file for font: '" + fontName + "'";
            byte[] fontData = inputStream.readAllBytes();
            ByteBuffer fontDataBuffer = MemoryUtil.memAlloc(fontData.length + 1);
            fontDataBuffer.put(fontData).put((byte) 0).flip();

            ByteBuffer fontNameData = MemoryUtil.memUTF8(fontName);
            if (nvgCreateFontMem(nvg, fontNameData, fontDataBuffer, true) == -1) {
                throw new IOException("Failed to load font: " + fontName);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
