package view;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import org.bytedeco.javacpp.IntPointer;
import java.util.Arrays;

import util.Config;
import model.universe.Chunk;
import model.universe.World;
import controller.Director;

import model.cells.CHolder;
import model.cells.Cell;

public class Movie {
    private static final int SCALE = Config.getInt("SCREEN_SCALE");
 
    private final int WORLD_WIDTH;
    private final int WORLD_HEIGHT;

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    private final int CHUNK_SIZE = Config.getInt("CHUNK_SIZE");

    // === Componentes de Renderização Otimizada ===
    private Texture texture;
    private int[] pixelBuffer;
    private IntPointer pixelPointer;
    private Vector2 texturePos;
    private int blackPixelInt;
 
    public Movie(int wd, int wh) {
      this.WORLD_WIDTH = wd * CHUNK_SIZE;
      this.WORLD_HEIGHT = wh * CHUNK_SIZE;
      this.SCREEN_WIDTH = WORLD_WIDTH * SCALE;
      this.SCREEN_HEIGHT = WORLD_HEIGHT * SCALE;
    }
 
    public float getDt() {
      return GetFrameTime();
    }

    public void init() {
        InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Falling Sand Engine");
        SetExitKey(KEY_NULL);

        int totalPixels = WORLD_WIDTH * WORLD_HEIGHT;
        pixelBuffer = new int[totalPixels];
        pixelPointer = new IntPointer(totalPixels);
        
        Image baseImage = GenImageColor(WORLD_WIDTH, WORLD_HEIGHT, BLACK);
        texture = LoadTextureFromImage(baseImage);
        UnloadImage(baseImage);

        texturePos = new Vector2().x(0).y(0);
        blackPixelInt = toRgbaInt(BLACK);
    }

    /**
     * Converte a cor do Raylib para um inteiro de 32 bits no formato R8G8B8A8 
     * compatível com a memória Little-Endian do processador e OpenGL.
     */
    private int toRgbaInt(Color color) {
        if (color == null) return 0xFF00FFFF; // Magenta para erros/nulo
        int r = color.r() & 0xFF;
        int g = color.g() & 0xFF;
        int b = color.b() & 0xFF;
        int a = color.a() & 0xFF;
        // Ordem dos bytes em memória LSB -> MSB: [R, G, B, A]
        return r | (g << 8) | (b << 16) | (a << 24);
    }

    /**
     * Busca a cor da célula no CHolder e salva no cache local.
     */
    private int getCellColorInt(int id) {
        if (id <= 0) { return blackPixelInt; }
        if (id == 0 ) { return blackPixelInt; }

        // Busca o objeto Cell através do CHolder
        Cell cell = CHolder.get(id);
        Color c = cell.getColor();
        int colorInt = toRgbaInt(c);
        return colorInt;
    }
 
    public void step(World w) {
        BeginDrawing();
        ClearBackground(BLACK);
        
        renderWorld(w);
        renderChunkBorders(); 
        renderDirtyRects(w);
        
        DrawText("FPS: " + GetFPS(), 10, 10, 20, WHITE);
        EndDrawing();
    }
 
    private void renderWorld(World w) {
        Arrays.fill(pixelBuffer, blackPixelInt);

        for (Chunk chunk : w.DATA.get()) {
            if (chunk == null) { continue; }

            int chunkOriginGX = (chunk.index() % w.width()) * CHUNK_SIZE;
            int chunkOriginGY = (chunk.index() / w.height()) * CHUNK_SIZE;

            for (int cy = 0; cy < CHUNK_SIZE; cy++) {
                for (int cx = 0; cx < CHUNK_SIZE; cx++) {
                    int id = chunk.getRawDataId(cx, cy);
                    if (id == 0) { continue; }

                    int gx = chunkOriginGX + cx;
                    int gy = chunkOriginGY + cy;
                    
                    // Escreve a cor da célula direto do cache
                    pixelBuffer[gy * WORLD_WIDTH + gx] = getCellColorInt(id);
                }
            }
        }

        pixelPointer.put(pixelBuffer, 0, pixelBuffer.length);
        UpdateTexture(texture, pixelPointer);
        
        DrawTextureEx(texture, texturePos, 0f, SCALE, WHITE);
    }

    private void renderDirtyRects(World w) {
        for (Chunk chunk : w.DATA.get()) { 
            if (chunk == null || chunk.RECT.isEmpty()) { continue; }

            int chunkOriginGX = (chunk.index() % w.width()) * CHUNK_SIZE;
            int chunkOriginGY = (chunk.index() / w.height()) * CHUNK_SIZE;

            int rectMinGX = chunkOriginGX + chunk.RECT.minCX();
            int rectMinGY = chunkOriginGY + chunk.RECT.minCY();
            int rectMaxGX = chunkOriginGX + chunk.RECT.maxCX();
            int rectMaxGY = chunkOriginGY + chunk.RECT.maxCY();

            int screenX = rectMinGX * SCALE;
            int screenY = rectMinGY * SCALE;
            int screenW = (rectMaxGX - rectMinGX + 1) * SCALE;
            int screenH = (rectMaxGY - rectMinGY + 1) * SCALE;

            DrawRectangleLines(screenX, screenY, screenW, screenH, RED);
        }
    }

    private void renderChunkBorders() {
        for (int gx = 0; gx <= WORLD_WIDTH; gx += CHUNK_SIZE) {
            int screenX = gx * SCALE;
            DrawLine(screenX, 0, screenX, SCREEN_HEIGHT, GREEN);
        }
        for (int gy = 0; gy <= WORLD_HEIGHT; gy += CHUNK_SIZE) {
            int screenY = gy * SCALE;
            DrawLine(0, screenY, SCREEN_WIDTH, screenY, GREEN);
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
      int radius = 30;
      int centerX = gx;
      int centerY = gy;

      for (int y = centerY - radius; y <= centerY + radius; y++) {
          for (int x = centerX - radius; x <= centerX + radius; x++) {
              int dx = x - centerX;
              int dy = y - centerY;
              if (dx*dx + dy*dy <= radius*radius) {
                  w.setWorldCellIn(x, y, 201, 0);
              }
          }
      }
    }
 
    private int screenToWorld(int screenCoord) {
        return screenCoord / SCALE;
    }

    public boolean shouldClose() {
        return WindowShouldClose();
    }
 
    public void close() {
        pixelPointer.close();
        UnloadTexture(texture);
        CloseWindow();
    }
}
