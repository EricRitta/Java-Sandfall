package view;
import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import util.Config;
import model.logic.Chunk;
import model.logic.World;
import controller.Director;

public class Movie {
    private static final int SCALE = Config.getInt("SCREEN_SCALE");
 
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private final int CHUNK_SIZE;
 
    public Movie(int wd, int wh) {
      this.WORLD_WIDTH = wd;
      this.WORLD_HEIGHT = wh;
      this.SCREEN_WIDTH = WORLD_WIDTH * SCALE;
      this.SCREEN_HEIGHT = WORLD_HEIGHT * SCALE;
      this.CHUNK_SIZE = Config.getInt("CHUNK_SIZE"); // ajusta a chave se for diferente no seu Config
    }
 
    public float getDt() {
      return GetFrameTime();
    }

    public void init() {
        InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Test Screen");
        SetTargetFPS(Config.getInt("TARGET_FPS"));
        SetExitKey(KEY_NULL);
    }
 
    public void step(World w) {
        BeginDrawing();
        ClearBackground(BLACK);
        renderWorld(w);
        renderChunkBorders(); // desenhado por cima, depois das células
        renderDirtyRects(w);
        DrawText("FPS: " + GetFPS(), 10, 10, 20, WHITE);
        EndDrawing();
    }
 
    private void renderDirtyRects(World w) {
        for (Chunk chunk : w.DATA) { // ajusta pro nome real do campo/getter de chunks no World
            if (chunk == null) { continue; }
            if (chunk.RECT.getIsEmpty()) { continue; } // ajusta se RECT tiver um getter em vez de ser público direto

            int chunkOriginGX = (chunk.getIndex() % w.getBoxSize()) * CHUNK_SIZE;
            int chunkOriginGY = (chunk.getIndex() / w.getBoxSize()) * CHUNK_SIZE;

            int rectMinGX = chunkOriginGX + chunk.RECT.getMinCX();
            int rectMinGY = chunkOriginGY + chunk.RECT.getMinCY();
            int rectMaxGX = chunkOriginGX + chunk.RECT.getMaxCX();
            int rectMaxGY = chunkOriginGY + chunk.RECT.getMaxCY();

            int screenX = rectMinGX * SCALE;
            int screenY = rectMinGY * SCALE;
            int screenW = (rectMaxGX - rectMinGX + 1) * SCALE;
            int screenH = (rectMaxGY - rectMinGY + 1) * SCALE;

            DrawRectangleLines(screenX, screenY, screenW, screenH, RED);
        }
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
                int id = w.getChunkDataIn(gx, gy, Chunk.CELL_ID);
 
                if (id == 0) { continue; }
 
                Color color = getColorForId(id);
                DrawRectangle(gx * SCALE, gy * SCALE, SCALE, SCALE, color);
            }
        }
    }

    // desenha uma linha na fronteira exata de cada chunk, pra visualizar onde uma célula
    // atravessa de um chunk pro outro
    private void renderChunkBorders() {
        // linhas verticais, a cada CHUNK_SIZE células
        for (int gx = 0; gx <= WORLD_WIDTH; gx += CHUNK_SIZE) {
            int screenX = gx * SCALE;
            DrawLine(screenX, 0, screenX, SCREEN_HEIGHT, GREEN);
        }

        // linhas horizontais, a cada CHUNK_SIZE células
        for (int gy = 0; gy <= WORLD_HEIGHT; gy += CHUNK_SIZE) {
            int screenY = gy * SCALE;
            DrawLine(0, screenY, SCREEN_WIDTH, screenY, GREEN);
        }
    }

    private Color getColorForId(int id) {
        switch (id) {
            case 1: return YELLOW;  // sand, exemplo
            case 201: return BLUE;    // water, exemplo
            case 3: return DARKGRAY; // metal, exemplo
            default: return MAGENTA; // cor "erro", fácil de notar se algum id não mapeado aparecer
        }
    }

    public void handleInput(World w, Director d) {
        if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
            Vector2 mousePos = GetMousePosition();
            int worldX = screenToWorld((int) mousePos.x());
            int worldY = screenToWorld((int) mousePos.y());
 
            if (worldX >= 0 && worldX < WORLD_WIDTH && worldY >= 0 && worldY < WORLD_HEIGHT) {
                onWorldClick(w, worldX, worldY);
            }
        }

        if (IsKeyPressed(KEY_ESCAPE)) {
          d.setPaused(!d.isPaused());
        }

        if (IsKeyPressed(KEY_SPACE)) {
          if (d.isPaused()) {
            d.step();
          }
        }
    }
 
    private void onWorldClick(World w, int gx, int gy) {
      int radius = 10; // raio do brush
      int centerX = gx;
      int centerY = gy;

      for (int y = centerY - radius; y <= centerY + radius; y++) {
          for (int x = centerX - radius; x <= centerX + radius; x++) {
              int dx = x - centerX;
              int dy = y - centerY;
              if (dx*dx + dy*dy <= radius*radius) {
                  w.setWorldCellIn(x, y, 1, 0);
              }
          }
      }
    }
 
    private int screenToWorld(int screenCoord) {
        return screenCoord / SCALE;
    }
}
