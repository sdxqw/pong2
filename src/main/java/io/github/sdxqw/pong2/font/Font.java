package io.github.sdxqw.pong2.font;

import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGTextRow;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        InputStream fontInputStream = null;
        try {
            fontInputStream = Utils.class.getResourceAsStream("/textures/fonts/" + fontName + ".ttf");
            if (fontInputStream == null) {
                throw new IOException("Font file not found: " + fontName);
            }

            // Create a temporary file to write the font data
            Path tempFontFile = Files.createTempFile(fontName, ".ttf");

            // Write the font data from the InputStream to the temporary file
            Files.copy(fontInputStream, tempFontFile, StandardCopyOption.REPLACE_EXISTING);

            // Convert the font file path to ByteBuffer
            String fontPath = tempFontFile.toAbsolutePath().toString();
            ByteBuffer fontPathData = MemoryUtil.memUTF8(fontPath);

            // Convert the font name to ByteBuffer
            ByteBuffer fontNameData = MemoryUtil.memUTF8(fontName);

            if (nvgCreateFont(nvg, fontNameData, fontPathData) == -1) {
                throw new IOException("Failed to load font: " + fontName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fontInputStream != null) {
                try {
                    fontInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
