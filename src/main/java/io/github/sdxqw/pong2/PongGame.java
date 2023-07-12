package io.github.sdxqw.pong2;

import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.rendering.FPS;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.score.Score;
import io.github.sdxqw.pong2.states.GameState;
import io.github.sdxqw.pong2.states.MainMenuState;
import io.github.sdxqw.pong2.states.PauseState;
import io.github.sdxqw.pong2.states.PlayState;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PongGame {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    private static final String WINDOW_TITLE = "Pong 2";

    public long window;
    public long vg;

    private Rendering renderer;
    public Font font;
    private GameState currentState;
    private FPS fpsCounter;
    public Score score;

    public boolean isGamePaused = false;
    private boolean isGameRunning = false;


    private double lastTime;


    public void startGame() {
        initGame();
        lastTime = glfwGetTime();
        currentState = new MainMenuState(this);

        while (isGameRunning && !glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            renderGame();
            updateGame(deltaTime);

            fpsCounter.update((float) deltaTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        endGame();
    }

    private void initGame() {
        isGameRunning = true;

        try {
            GLFWErrorCallback.createPrint(System.err).set();
            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }

            createWindow();
            setupCallbacks();

            glfwMakeContextCurrent(window);
            GL.createCapabilities();

            vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
            if (vg == NULL) {
                throw new RuntimeException("Failed to create NanoVG context");
            }

            glfwShowWindow(window);

            score = new Score();
            fpsCounter = new FPS(0.5f);
            renderer = new Rendering();
            font = new Font(vg, "pixel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastTime = glfwGetTime();
    }

    private void createWindow() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
    }

    private void renderGame() {
        glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        nvgBeginFrame(vg, WINDOW_WIDTH, WINDOW_HEIGHT, 1.0f);

        currentState.render(renderer, vg);

        String fps = String.format("FPS: %.0f", fpsCounter.getFPS());
        font.drawText(fps, NVG_ALIGN_BASELINE, WINDOW_HEIGHT - 15, 25, 22, Utils.color(1f, 1f, 1f, 1f));

        nvgEndFrame(vg);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    private void updateGame(double deltaTime) {
        currentState.update(deltaTime);
    }

    private void setupCallbacks() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (currentState instanceof MainMenuState) {
                ((MainMenuState) currentState).onKeyPressed(key, action);
            }

            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                if (!(currentState instanceof PauseState) && !(currentState instanceof MainMenuState)) {
                    isGamePaused = true;
                    changeState(new PauseState(this, (PlayState) currentState));
                }
            }

            if (currentState instanceof PauseState) {
                ((PauseState) currentState).onKeyPressed(key, action);
            }

            if (key == GLFW_KEY_LEFT_SHIFT && action == GLFW_RELEASE && currentState instanceof PlayState) {
                ((PlayState) currentState).ball.incrementSpeed();
            }

            if (key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS && currentState instanceof PlayState) {
                ((PlayState) currentState).ball.resetSpeed();
            }
        });
    }

    public void endGame() {
        isGameRunning = false;
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        nvgDelete(vg);
        glfwTerminate();
        window = NULL;
        vg = NULL;
        System.exit(0);
    }

    public void changeState(GameState nextState) {
        currentState = nextState;
    }
}
