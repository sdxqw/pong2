package io.github.sdxqw.pong2.utils;

import io.github.sdxqw.pong2.handling.ResourceLocation;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.nanovg.NanoVG.*;

public class Utils {

    public static GLFWImage.Buffer loadImage(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer imageBuffer = null;
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            // Load the image using STBImage
            try (InputStream in = Utils.class.getClassLoader().getResourceAsStream(path);
                 BufferedInputStream bufferedIn = new BufferedInputStream(Objects.requireNonNull(in))) {

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int bytesRead;
                byte[] data = new byte[1024];
                while ((bytesRead = bufferedIn.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }
                buffer.flush();
                byte[] imageBytes = buffer.toByteArray();

                imageBuffer = MemoryUtil.memAlloc(imageBytes.length);
                imageBuffer.put(imageBytes);
                imageBuffer.flip();

                ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 4);
                if (image == null) {
                    throw new RuntimeException("Failed to load image: " + path);
                }

                // Create a GLFWImage to hold the loaded image
                GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
                iconBuffer.position(0);
                iconBuffer.width(w.get(0));
                iconBuffer.height(h.get(0));
                iconBuffer.pixels(image);

                return iconBuffer;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (imageBuffer != null) {
                    MemoryUtil.memFree(imageBuffer);
                }
            }
        }
    }
    

    public static void drawImage(long nvg, ResourceLocation location, float x, float y, float width, float height, int alpha, float[] color) {
        nvgBeginPath(nvg);
        NVGPaint paint = nvgImagePattern(nvg, x, y, width, height, 0.0F, location.getId(), alpha / 255.0F, NVGPaint.create());
        paint.innerColor(color(color[0], color[1], color[2], color[3]));
        paint.outerColor(color(color[0], color[1], color[2], color[3]));
        nvgRect(nvg, x, y, width, height);
        nvgFillPaint(nvg, paint);
        nvgFill(nvg);
    }

    public static NVGColor color(float r, float g, float b, float a) {
        return NVGColor.calloc().a(a).r(r).g(g).b(b);
    }
}
