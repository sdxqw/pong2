package io.github.sdxqw.pong2.input;

import org.lwjgl.glfw.GLFW;

public class InputManager {
    private final long window;

    public InputManager(long window) {
        this.window = window;
    }

    public boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
    }
}
