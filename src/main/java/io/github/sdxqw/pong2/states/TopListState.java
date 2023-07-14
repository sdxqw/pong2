package io.github.sdxqw.pong2.states;

import io.github.sdxqw.pong2.PongGame;
import io.github.sdxqw.pong2.rendering.Rendering;
import io.github.sdxqw.pong2.utils.Utils;
import lombok.SneakyThrows;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

public class TopListState extends GameState {
    private double blinkTimer = 0;
    private boolean isTextVisible = true;
    private double deltaTime;

    public TopListState(PongGame game) {
        super(game);
    }

    @SneakyThrows
    @Override
    public void render(Rendering renderer, long vg) {
        float startX = (float) PongGame.WINDOW_WIDTH / 2 - 210;
        float startY = (float) PongGame.WINDOW_HEIGHT / 2 - 230;
        game.font.drawText("TOP-LIST PLAYERS", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER, startX + 210, startY - 70, 40, Utils.color(1f, 1f, 1f, 1f));

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


        if (!game.server.isConnectionAlive()) {
            if (isTextVisible) {
                game.font.drawText("No Database Connection :(", NVG_ALIGN_MIDDLE | NVG_ALIGN_CENTER,
                        (float) PongGame.WINDOW_WIDTH / 2, (float) PongGame.WINDOW_HEIGHT / 2, 50,
                        Utils.color(0.3f, 0.3f, 0.3f, 0.8f));
            }
        }

        /*
         STILL TO FIX.

         List<String> allUserNames = game.server.getAllUserNames();
         List<Integer> highestScores = game.server.getHighestScores();

         List<Integer> indices = new ArrayList<>();
         for (int i = 0; i < allUserNames.size(); i++) {
         indices.add(i);
         }

         indices.sort((i1, i2) -> highestScores.get(i2).compareTo(highestScores.get(i1)));

         int counter = 0;
         for (int i : indices) {
         if (counter < 10) {
         float yPos = startY + counter * 30;
         if (game.userName.equals(allUserNames.get(i))) {
         if (isTextVisible)
         game.font.drawText(">", NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX - 20, yPos - 0.5f, 32, Utils.color(1f, 1f, 0f, 1f));
         game.font.drawText(allUserNames.get(i), NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX, yPos, 28, Utils.color(1f, 1f, 1f, 1f));
         } else
         game.font.drawText(allUserNames.get(i), NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_MIDDLE, startX, yPos, 28, Utils.color(1f, 1f, 1f, 1f));
         float xPos = startX + 485;
         game.font.drawText(String.valueOf(highestScores.get(i)), NanoVG.NVG_ALIGN_RIGHT | NanoVG.NVG_ALIGN_MIDDLE, xPos, yPos, 28, Utils.color(0.6f, 0.2f, 0.4f, 0.6f));
         counter++;
         } else {
         break;
         }
         }
         */
    }

    @Override
    public void update(double deltaTime) {
        this.deltaTime = deltaTime;
    }
}
