package view;
import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import model.logic.World;

public class Movie {
    private static final int SCALE = 8; // fator de escala tela/mundo
 
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
 
    public Movie(int wd, int wh) {
      this.WORLD_WIDTH = wd;
      this.WORLD_HEIGHT = wh;
      this.SCREEN_WIDTH = WORLD_WIDTH * SCALE;
      this.SCREEN_HEIGHT = WORLD_HEIGHT * SCALE;
    }
 
    public void init() {
        InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Test Screen");
        SetTargetFPS(120);
    }
 
    public void step(World w) {
        BeginDrawing();
        ClearBackground(BLACK);
        renderWorld(w);
        EndDrawing();
    }
 
    public boolean shouldClose() {
        return WindowShouldClose();
    }
 
    public void close() {
        CloseWindow();
    }
 
    private void renderWorld(World w) {
        for (int gy = 0; gy < WORLD_HEIGHT; gy++) {
            for (int gx = 0; gx < WORLD_WIDTH; gx++) {
                int id = w.getWorldCellIn(gx, gy); // ajusta se o método/assinatura real for diferente
 
                if (id == 0) { continue; } // não desenha ar, só o que tem partícula
 
                Color color = getColorForId(id);
                DrawRectangle(gx * SCALE, gy * SCALE, SCALE, SCALE, color);
            }
        }
    }

    private Color getColorForId(int id) {
        switch (id) {
            case 1: return YELLOW;  // sand, exemplo
            case 2: return BLUE;    // water, exemplo
            case 3: return DARKGRAY; // metal, exemplo
            default: return MAGENTA; // cor "erro", fácil de notar se algum id não mapeado aparecer
        }
    }

    public void handleInput(World w) {
        if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
            Vector2 mousePos = GetMousePosition();
            int worldX = screenToWorld((int) mousePos.x());
            int worldY = screenToWorld((int) mousePos.y());
 
            if (worldX >= 0 && worldX < WORLD_WIDTH && worldY >= 0 && worldY < WORLD_HEIGHT) {
                onWorldClick(w, worldX, worldY);
            }
        }
    }
 
    // TODO: pra teste, só imprime. Depois troca por world.setCellIn(worldX, worldY, ...)
    private void onWorldClick(World w, int gx, int gy) {
      w.setWorldCellIn(gx, gy, 1, 0);
    }
 
    private int screenToWorld(int screenCoord) {
        return screenCoord / SCALE;
    }
}
