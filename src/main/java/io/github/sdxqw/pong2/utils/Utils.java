package io.github.sdxqw.pong2.utils;

import io.github.sdxqw.pong2.rendering.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import static org.lwjgl.nanovg.NanoVG.*;

public class Utils {
    public static ByteBuffer readFile(Path path) throws IOException {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = fc.size();
            if (fileSize == 0) {
                throw new IOException("File is empty: " + path);
            }

            ByteBuffer buffer = BufferUtils.createByteBuffer((int) fileSize);
            fc.read(buffer);
            buffer.flip();
            return buffer;
        }
    }

    public static GLFWImage.Buffer loadImage(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            // Load the image using STBImage
            ByteBuffer imageBuffer = Utils.readFile(Paths.get(Objects.requireNonNull(Utils.class.getResource(path)).toURI()));
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
