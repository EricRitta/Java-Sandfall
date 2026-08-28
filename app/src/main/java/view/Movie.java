package view;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;
import org.bytedeco.javacpp.IntPointer;
import java.util.Arrays;

import util.Config;
import model.universe.Chunk;
import model.universe.World;
import controller.Director;

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

        // Inicializa o buffer na RAM (Java) e o Ponteiro do JavaCPP
        int totalPixels = WORLD_WIDTH * WORLD_HEIGHT;
        pixelBuffer = new int[totalPixels];
        pixelPointer = new IntPointer(totalPixels);
        
        // Cria uma imagem preta base e envia para a VRAM (GPU)
        Image baseImage = GenImageColor(WORLD_WIDTH, WORLD_HEIGHT, BLACK);
        texture = LoadTextureFromImage(baseImage);
        UnloadImage(baseImage); // Libera a imagem base da RAM

        texturePos = new Vector2().x(0).y(0);
        blackPixelInt = ColorToInt(BLACK);
    }
 
    public void step(World w) {
        BeginDrawing();
        ClearBackground(BLACK);
        
        renderWorld(w);
        // renderChunkBorders(); 
        // renderDirtyRects(w);
        
        DrawText("FPS: " + GetFPS(), 10, 10, 20, WHITE);
        EndDrawing();
    }
 
    private void renderWorld(World w) {
        // 1. Limpa o buffer de pixels na memória RAM instantaneamente (sem JNI)
        Arrays.fill(pixelBuffer, blackPixelInt);

        // 2. Percorre diretamente os Chunks ao invés de usar coordenadas globais
        for (Chunk chunk : w.DATA.get()) {
            if (chunk == null) { continue; }

            // Pré-calcula a origem global do chunk para evitar matemática no loop interno
            int chunkOriginGX = (chunk.index() % w.width()) * CHUNK_SIZE;
            int chunkOriginGY = (chunk.index() / w.height()) * CHUNK_SIZE;

            // 3. Lê os dados brutos da array do Chunk
            for (int cy = 0; cy < CHUNK_SIZE; cy++) {
                for (int cx = 0; cx < CHUNK_SIZE; cx++) {
                    int id = chunk.getRawDataId(cx, cy);
                    if (id == 0) { continue; }

                    int gx = chunkOriginGX + cx;
                    int gy = chunkOriginGY + cy;
                    
                    // Converte a cor para inteiro e escreve na array plana
                    pixelBuffer[gy * WORLD_WIDTH + gx] = ColorToInt(getColorForId(id));
                }
            }
        }

        // 4. Copia a array do Java para o JavaCPP e atualiza a GPU em uma tacada só
        pixelPointer.put(pixelBuffer, 0, pixelBuffer.length);
        UpdateTexture(texture, pixelPointer);
        
        // 5. Desenha a Textura final escalonada na tela
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

    private Color getColorForId(int id) {
        switch (id) {
            case 1: return YELLOW;
            case 201: return YELLOW; 
            case 3: return DARKGRAY;
            default: return MAGENTA;
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
        pixelPointer.close(); // Previne vazamento de memória do C++
        UnloadTexture(texture);
        CloseWindow();
    }
}
