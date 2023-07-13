package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

public class KeyListState extends GameState {
    public final Map<Integer, Integer> keyBinds;
    private final List<String> keyBindsList;
    private final List<Integer> movableIndices;
    public boolean isFirstEnter = true;
    private double blinkTimer = 0;
    private boolean isTextVisible = true;
    private double deltaTime;
    private int selectedIndex = 0;
    private boolean isAwaitingNewKey = false;
    private boolean alreadyBound = false;

    public KeyListState(PongGame game) {
        super(game);

        keyBindsList = new ArrayList<>();
        keyBinds = new HashMap<>();

        keyBindsList.add("Reset Ball Speed:");
        keyBindsList.add("Increase Ball Speed:");
        keyBindsList.add("Show FPS:");
        keyBindsList.add("Restart game:");

        keyBindsList.add("Move Up:");
        keyBindsList.add("Move Down:");
        keyBindsList.add("Interact:");
        keyBindsList.add("Interact 2:");
        keyBindsList.add("Show Pause Menu:");
        keyBindsList.add("Go Back:");

        keyBinds.put(0, GLFW_KEY_LEFT_SHIFT);
        keyBinds.put(1, GLFW_KEY_LEFT_CONTROL);
        keyBinds.put(2, GLFW_KEY_L);

        keyBinds.put(3, GLFW_KEY_SPACE);
        keyBinds.put(4, GLFW_KEY_W);
        keyBinds.put(5, GLFW_KEY_S);
        keyBinds.put(6, GLFW_KEY_SPACE);
        keyBinds.put(7, GLFW_KEY_ENTER);
        keyBinds.put(8, GLFW_KEY_ESCAPE);
        keyBinds.put(9, GLFW_KEY_ESCAPE);
        keyBinds.put(10, GLFW_KEY_P);

        movableIndices = new ArrayList<>();
        movableIndices.add(0);
        movableIndices.add(1);
        movableIndices.add(2);
    }

    static int getMaxWordLength(List<String> keyBindsList) {
        int maxLength = 0;

        for (String key : keyBindsList) {
            int keyLength = key.trim().length();
            maxLength = Math.max(maxLength, keyLength);
        }

        return maxLength;
    }

    @Override
    public void render(Rendering renderer, long vg) {
        float startX = (float) PongGame.WINDOW_WIDTH / 2 - 255;
        float startY = (float) PongGame.WINDOW_HEIGHT / 2 - 230;
        game.font.drawText("KEYBINDING LIST", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, startX + 255, startY - 70, 40, Utils.color(1f, 1f, 1f, 1f));

        int fontSize = 22;
        int lineHeight = 30;

        NVGColor color = Utils.color(1f, 1f, 1f, 1f);

        int maxWordLength = getMaxWordLength(keyBindsList);
        for (int i = 0; i < keyBindsList.size(); i++) {
            String key = keyBindsList.get(i);
            Integer value = keyBinds.get(i);

            float yPos = startY + i * lineHeight + 15;

            // Render key
            if (selectedIndex == i) {
                game.font.drawText("> ", NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX + 20, yPos, fontSize, color);
                if (isAwaitingNewKey)
                    game.font.drawText(key, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX + 45, yPos, fontSize, Utils.color(0.2f, 0.5f, 0.3f, 1f));
            }

            if (!isAwaitingNewKey || selectedIndex != i)
                game.font.drawText(key, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX + 45, yPos, fontSize, color);

            if (isAwaitingNewKey && !alreadyBound)
                game.font.drawText("Waiting for key...", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 + 290, 22,
                        Utils.color(0.6f, 0.6f, 0.6f, 0.6f));

            if (alreadyBound)
                game.font.drawText("Key already bound!", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 + 290, 22,
                        Utils.color(0.6f, 0.6f, 0.6f, 0.6f));
            // Calculate the position for the value based on the maximum key length
            float valueXPos = startX + maxWordLength * 10 + 200;
            game.font.drawText(convertKeyToString(value), NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_MIDDLE, valueXPos + 30, yPos, fontSize, Utils.color(0.2f, 0.5f, 0.3f, 1f));
        }

        blinkTimer += deltaTime;
        if (blinkTimer >= 0.3f) {
            isTextVisible = !isTextVisible;
            blinkTimer = 0;
        }

        if (isTextVisible) {
            game.font.drawText("Press ESC to go back", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                    (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2 + 320, 22,
                    Utils.color(0.8f, 0.8f, 0.8f, 0.8f));
        }
    }

    public int getValueByIndex(int index) {
        return keyBinds.getOrDefault(index, -1);
    }

    public String convertKeyToString(int key) {
        return switch (key) {
            case GLFW_KEY_A -> "A";
            case GLFW_KEY_B -> "B";
            case GLFW_KEY_C -> "C";
            case GLFW_KEY_D -> "D";
            case GLFW_KEY_E -> "E";
            case GLFW_KEY_F -> "F";
            case GLFW_KEY_G -> "G";
            case GLFW_KEY_H -> "H";
            case GLFW_KEY_I -> "I";
            case GLFW_KEY_J -> "J";
            case GLFW_KEY_K -> "K";
            case GLFW_KEY_L -> "L";
            case GLFW_KEY_M -> "M";
            case GLFW_KEY_N -> "N";
            case GLFW_KEY_O -> "O";
            case GLFW_KEY_P -> "P";
            case GLFW_KEY_Q -> "Q";
            case GLFW_KEY_R -> "R";
            case GLFW_KEY_S -> "S";
            case GLFW_KEY_T -> "T";
            case GLFW_KEY_U -> "U";
            case GLFW_KEY_V -> "V";
            case GLFW_KEY_W -> "W";
            case GLFW_KEY_X -> "X";
            case GLFW_KEY_Y -> "Y";
            case GLFW_KEY_Z -> "Z";
            case GLFW_KEY_0 -> "0";
            case GLFW_KEY_1 -> "1";
            case GLFW_KEY_2 -> "2";
            case GLFW_KEY_3 -> "3";
            case GLFW_KEY_4 -> "4";
            case GLFW_KEY_5 -> "5";
            case GLFW_KEY_6 -> "6";
            case GLFW_KEY_7 -> "7";
            case GLFW_KEY_8 -> "8";
            case GLFW_KEY_9 -> "9";
            case GLFW_KEY_SPACE -> "SPACE";
            case GLFW_KEY_ENTER -> "ENTER";
            case GLFW_KEY_ESCAPE -> "ESC";
            case GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW_KEY_RIGHT_ALT -> "RALT";
            default -> "Unknown key";
        };
    }

    public void modifyKeybinding(int newKeyCode) {
        if (isAwaitingNewKey) {
            if (!isKeyAlreadyBound(newKeyCode)) {
                keyBinds.put(selectedIndex, newKeyCode);
                isAwaitingNewKey = false;
            } else alreadyBound = true;
        }
    }

    public void onKeyPressed(int key, int action) {
        if (action == GLFW_PRESS) {
            if (isFirstEnter && key == GLFW_KEY_ENTER) {
                isFirstEnter = false;
                return;
            }

            if (key == GLFW_KEY_W && movableIndices.contains(selectedIndex)) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                }
            } else if (key == GLFW_KEY_S && movableIndices.contains(selectedIndex)) {
                if (selectedIndex < movableIndices.size() - 1) {
                    selectedIndex++;
                }
            }

            if (key == GLFW_KEY_BACKSPACE)
                isAwaitingNewKey = false;

            if (isAwaitingNewKey) {
                if (key != GLFW_KEY_ENTER && key != GLFW_KEY_SPACE) {
                    modifyKeybinding(key);
                }
            } else {
                if (key == GLFW_KEY_ENTER || key == GLFW_KEY_SPACE) {
                    if (movableIndices.contains(selectedIndex)) {
                        alreadyBound = false;
                        isAwaitingNewKey = true;
                    }
                }
            }
        }
    }

    private boolean isKeyAlreadyBound(int keyCode) {
        return keyBinds.containsValue(keyCode);
    }


    @Override
    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
    }
}
