package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

public class KeyListState extends GameState {
    private final Map<String, String> keyBindsList;

    private double blinkTimer = 0;
    private boolean isTextVisible = true;
    private double deltaTime;

    public KeyListState(PongGame game) {
        super(game);

        keyBindsList = new HashMap<>();
        keyBindsList.put("Reset Ball Speed:", "Left-Ctrl");
        keyBindsList.put("Increase Ball Speed:", "Left-Shift");
        keyBindsList.put("Show FPS:", "L");
        keyBindsList.put("Show Pause Menu:", "ESC or ESCAPE");
        keyBindsList.put("Go Back:", "ESC or ESCAPE");
    }

    @Override
    public void render(Rendering renderer, long vg) {
        float startX = (float) PongGame.WINDOW_WIDTH / 2 - 255;
        float startY = (float) PongGame.WINDOW_HEIGHT / 2 - 230;
        game.font.drawText("KEYBINDING LIST", NanoVG.NVG_ALIGN_MIDDLE | NanoVG.NVG_ALIGN_LEFT, startX + 55, startY - 70, 40, Utils.color(1f,1f , 1f, 1f));

        int fontSize = 22;
        int lineHeight = 30;

        NVGColor color = Utils.color(1f, 1f, 1f, 1f);

        int i = 0;
        int maxWordLength = getMaxWordLength(keyBindsList);
        for (Map.Entry<String, String> entry : keyBindsList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            float yPos = startY + i * lineHeight;

            // Render key
            game.font.drawText(key, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX, yPos, fontSize, color);

            // Calculate the position for the value based on the maximum key length
            float valueXPos = startX + maxWordLength * 10 + 200;
            game.font.drawText(value, NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_MIDDLE, valueXPos, yPos, fontSize, Utils.color(0.2f, 0.5f, 0.3f, 1f));

            i++;
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

    private int getMaxWordLength(Map<String, String> keyBindsList) {
        return getMaxWords(keyBindsList);
    }

    static int getMaxWords(Map<String, String> keyBindsList) {
        int maxLength = 0;

        for (Map.Entry<String, String> entry : keyBindsList.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            int keyLength = key.trim().length();
            int valueLength = value.trim().length();

            maxLength = Math.max(maxLength, keyLength + valueLength);
        }

        return maxLength;
    }

    @Override
    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
    }
}
