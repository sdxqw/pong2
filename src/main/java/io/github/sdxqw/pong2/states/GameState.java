package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.rendering.Rendering;

public abstract class GameState {
    public final PongGame game;

    public GameState(PongGame game) {
        this.game = game;
    }

    public abstract void render(Rendering renderer, long vg);

    public abstract void update(double deltaTime);
}
