package io.github.sdxqw.pong2.rendering;

import io.github.sdxqw.pong2.utils.Utils;
import lombok.Getter;

public class Image {
    private final ResourceManager resourceManager;
    @Getter
    private final ResourceLocation location;

    public Image(ResourceManager resourceManager, ResourceLocation location) {
        this.resourceManager = resourceManager;
        this.location = location;
        location.setId(resourceManager.loadImage(location.getPath()));
    }

    public void render(float x, float y, float width, float height, float[] color) {
        Utils.drawImage(resourceManager.getNvg(), location, x, y, width, height, 255, color);
    }

    public void cleanup() {
        resourceManager.deleteImage(location.getId());
    }
}