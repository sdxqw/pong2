package io.github.sdxqw.pong2.rendering;

public class FPS {
    private final float updateInterval;
    private int frameCount;
    private float elapsed;
    private float fps;

    public FPS(float updateInterval) {
        this.updateInterval = updateInterval;
        reset();
    }

    public void update(float deltaTime) {
        frameCount++;
        elapsed += deltaTime;

        if (elapsed >= updateInterval) {
            fps = frameCount / elapsed;
            reset();
        }
    }

    public float getFPS() {
        return fps;
    }

    private void reset() {
        frameCount = 0;
        elapsed = 0;
    }
}

