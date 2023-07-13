package io.github.sdxqw.pong2;

import io.github.sdxqw.pong2.data.UserData;
import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.input.InputManager;
import io.github.sdxqw.pong2.rendering.FPS;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.score.Score;
import io.github.sdxqw.pong2.server.PongServer;
import io.github.sdxqw.pong2.states.*;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;

import java.util.UUID;

import static io.github.sdxqw.pong2.utils.Utils.loadImage;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PongGame {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    private static final String WINDOW_TITLE = "Pong 2";

    public String userName = "User";

    public long window;
    public long vg;
    public Font font;
    public Score score;
    public InputManager inputManager;
    public boolean isGamePaused = false;
    public boolean showPauseMenu = true;
    public PongServer server;
    private Rendering renderer;
    private GameState currentState;
    private FPS fpsCounter;
    private UserData userData;

    private boolean isGameRunning = false;
    private boolean showFPS = true;

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
            inputManager = new InputManager(window);

            server = new PongServer();
            userData = new UserData();

            userData.loadSessionID();

            if (userData.getSessionID() != null) {
                // Load existing session from database using session ID
                server.loadSessionFromDatabase(this, userData.getSessionID());
            } else {
                // Generate new session ID
                UUID sessionID = UUID.randomUUID();
                userData.setSessionID(sessionID);
            }

            GLFWImage.Buffer iconBuffer = loadImage("/textures/image/icon.png");
            glfwSetWindowIcon(window, iconBuffer);

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

        if (showFPS) {
            String fps = String.format("FPS: %.0f", fpsCounter.getFPS());
            font.drawText(fps, NVG_ALIGN_BASELINE, 15, WINDOW_HEIGHT - 20, 22, Utils.color(1f, 1f, 1f, 0.2f));
        }

        nvgEndFrame(vg);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    private void updateGame(double deltaTime) {
        currentState.update(deltaTime);
    }

    private void setupCallbacks() {
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (currentState instanceof MainMenuState)
                ((MainMenuState) currentState).onKeyPressed(key, action);

            if (inputManager.isKeyPressed(GLFW_KEY_ESCAPE)) {
                if (!(currentState instanceof PauseState) && !(currentState instanceof MainMenuState)
                        && !(currentState instanceof KeyListState) && !(currentState instanceof TopListState) && showPauseMenu) {
                    isGamePaused = true;
                    changeState(new PauseState(this, (PlayState) currentState));
                }

                if (currentState instanceof KeyListState)
                    changeState(new MainMenuState(this));

                if (currentState instanceof TopListState)
                    changeState(new MainMenuState(this));
            }

            if (currentState instanceof PauseState)
                ((PauseState) currentState).onKeyPressed(key, action);

            if (inputManager.isKeyPressed(GLFW_KEY_LEFT_SHIFT) && currentState instanceof PlayState)
                ((PlayState) currentState).ball.incrementSpeed(2f);

            if (inputManager.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && currentState instanceof PlayState)
                ((PlayState) currentState).ball.resetSpeed();

            if (inputManager.isKeyPressed(GLFW_KEY_L))
                showFPS = !showFPS;
        });
    }

    public void endGame() {
        isGameRunning = false;

        // Save session ID to JSON
        userData.saveSessionID();

        // Save session data to the database
        server.saveSessionToDatabase(userName, score, userData.getSessionID());

        server.closeConnection();
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
