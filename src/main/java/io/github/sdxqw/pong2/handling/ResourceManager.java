package io.github.sdxqw.pong2.handling;

import io.github.sdxqw.logger.Logger;
import io.github.sdxqw.pong2.utils.Utils;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.nanovg.NanoVG.nvgCreateImageMem;
import static org.lwjgl.nanovg.NanoVG.nvgDeleteImage;

public class ResourceManager {
    private static final int NVG_IMAGE_GENERATE_MIPMAPS = 1;
    @Getter
    private final long nvg;
    private final Map<String, Integer> loadedImages;

    public ResourceManager(long nvg) {
        this.nvg = nvg;
        this.loadedImages = new HashMap<>();
    }

    public int loadImage(String imagePath) {
        if (loadedImages.containsKey(imagePath)) {
            return loadedImages.get(imagePath);
        }

        try (InputStream in = Utils.class.getResourceAsStream(imagePath);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            int bytesRead;
            byte[] data = new byte[1024];
            while ((bytesRead = Objects.requireNonNull(in).read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            buffer.flush();
            byte[] imageBytes = buffer.toByteArray();

            ByteBuffer imageBuffer = ByteBuffer.allocateDirect(imageBytes.length);
            imageBuffer.put(imageBytes);
            imageBuffer.flip();

            int imageId = nvgCreateImageMem(nvg, NVG_IMAGE_GENERATE_MIPMAPS, imageBuffer);
            loadedImages.put(imagePath, imageId);
            return imageId;
        } catch (IOException | NullPointerException e) {
            Logger.error("Failed to load image: %s", imagePath);
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteImage(int imageId) {
        nvgDeleteImage(nvg, imageId);
    }
}
