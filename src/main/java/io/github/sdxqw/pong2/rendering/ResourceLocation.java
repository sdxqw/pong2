package io.github.sdxqw.pong2.rendering;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ResourceLocation {
    private final String path;
    private int id;

    public ResourceLocation(String path) {
        this.path = path;
    }
}
