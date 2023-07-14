package io.github.sdxqw.pong2;

import io.github.sdxqw.logger.Logger;
import io.github.sdxqw.pong2.data.UserData;
import io.github.sdxqw.pong2.font.Font;
import io.github.sdxqw.pong2.input.InputManager;
import io.github.sdxqw.pong2.rendering.FPS;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.score.Score;
import io.github.sdxqw.pong2.server.PongServer;
import io.github.sdxqw.pong2.states.*;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;

import static io.github.sdxqw.pong2.utils.Utils.loadImage;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class PongGame {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    private static final String WINDOW_TITLE = "Pong 2";
    public InputManager inputManager;
    public GameState currentState;
    public KeyListState keyListState;
    public UserData userData;
    public PongServer server;
    public Score score;
    public boolean isGamePaused = false;
    public long vg;
    public Font font;
    private long window;
    private FPS fpsCounter;
    private boolean showFPS = true;
    private double lastTime;

    public void startGame() {
        initGame();
        lastTime = glfwGetTime();
        currentState = new MainMenuState(this);

        while (!glfwWindowShouldClose(window)) {
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
        try {
            GLFWErrorCallback.createPrint(System.err).set();
            if (!glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }
            createWindow();
            setupCallbacks();

            glfwMakeContextCurrent(window);
            GL.createCapabilities();

            initNanoVG();

            glfwShowWindow(window);

            initialize();

            setWindowIcon();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastTime = glfwGetTime();
    }

    private void createWindow() {
        Logger.info("Creating window...");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        Logger.info("Window created");
    }

    private void initNanoVG() {
        Logger.info("Initializing NanoVG...");
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == NULL) {
            throw new RuntimeException("Failed to create NanoVG context");
        }
        Logger.info("NanoVG initialized");
    }

    private void setWindowIcon() {
        Logger.info("Setting window icon...");
        GLFWImage.Buffer iconBuffer = loadImage("textures/image/icon.png");
        glfwSetWindowIcon(window, iconBuffer);
        Logger.info("Window icon set");
    }

    private void renderGame() {
        prepareRendering();

        nvgBeginFrame(vg, WINDOW_WIDTH, WINDOW_HEIGHT, 1.0f);

        currentState.render(new Rendering(), vg);

        if (showFPS) {
            String fps = String.format("FPS: %.0f", fpsCounter.getFPS());
            font.drawText(fps, NVG_ALIGN_BASELINE, 15, WINDOW_HEIGHT - 20, 22, Utils.color(1f, 1f, 1f, 0.2f));
        }

        nvgEndFrame(vg);

        finishRendering();
    }

    private void prepareRendering() {
        glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void finishRendering() {
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    private void updateGame(double deltaTime) {
        currentState.update(deltaTime);
    }

    private void setupCallbacks() {
        Logger.info("Setting up callbacks...");
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (currentState instanceof MainMenuState)
                ((MainMenuState) currentState).onKeyPressed(key, action);

            if (inputManager.isKeyPressed(keyListState.getValueByIndex(8))) {
                if (!(currentState instanceof PauseState) && !(currentState instanceof MainMenuState)
                        && !(currentState instanceof KeyListState) && !(currentState instanceof TopListState) && !(currentState instanceof GameModeState)) {
                    isGamePaused = true;
                    changeState(new PauseState(this, (PlayState) currentState));
                }

                if (currentState instanceof KeyListState) {
                    keyListState.isFirstEnter = true;
                    changeState(new MainMenuState(this));
                }

                if (currentState instanceof GameModeState) {
                    ((GameModeState) currentState).firstClick = true;
                    changeState(new MainMenuState(this));
                }

                if (currentState instanceof TopListState)
                    changeState(new MainMenuState(this));
            }

            if (currentState instanceof KeyListState)
                ((KeyListState) currentState).onKeyPressed(key, action);

            if (currentState instanceof GameModeState) {
                ((GameModeState) currentState).onKeyPressed(key, action);
            }

            if (currentState instanceof PauseState)
                ((PauseState) currentState).onKeyPressed(key, action);

            if (inputManager.isKeyPressed(keyListState.getValueByIndex(1)) && currentState instanceof PlayState)
                ((PlayState) currentState).ball.incrementSpeed(2f);

            if (inputManager.isKeyPressed(keyListState.getValueByIndex(0)) && currentState instanceof PlayState)
                ((PlayState) currentState).ball.resetSpeed(this);

            if (inputManager.isKeyPressed(keyListState.getValueByIndex(2)))
                showFPS = !showFPS;
        });
        Logger.info("Callbacks set");
    }

    public void endGame() {
        Logger.info("Ending game...");
        userData.saveSessionID();
        server.saveHighestScore(userData.getSessionID(), score.getHighestPlayer1Score());
        server.closeConnection();
        cleanup();
        Logger.info("Game ended");
        System.exit(0);
    }

    private void cleanup() {
        Logger.info("Cleaning up...");
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        nvgDelete(vg);
        glfwTerminate();
        Logger.info("Cleaned up");
    }

    public void initialize() {
        Logger.info("Initializing game...");
        score = new Score();
        font = new Font(vg, "pixel");
        inputManager = new InputManager(window);
        keyListState = new KeyListState(this);
        fpsCounter = new FPS(0.35f);
        userData = new UserData();
        userData.loadSessionID();
        server = new PongServer(this);
        server.getUserName(userData.getSessionID());
        server.getHighestScore(userData.getSessionID());
        Logger.info("Game initialized");
    }

    public void changeState(GameState nextState) {
        currentState = nextState;
    }
}
