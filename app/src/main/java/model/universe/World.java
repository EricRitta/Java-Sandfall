package model.universe;

// JAVA
import java.util.Random;

// PROJECT
import model.extenders.ChunkCSOA;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.function.Consumer;

public class World {
  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  // private final int NUM_THREADS;
  // private final ExecutorService POOL;

  private final int CHUNK_SIZE;
  private final int WIDTH;
  private final int HEIGHT;
  private final int DATA_SIZE;
  private final ChunkSOA DATA;

  //== VARIABLES ==//
  private int time = 1;

  //== CALL METHOD ==//
  public World(int cs, int ww, int wh) {
    this.CHUNK_SIZE = cs;
    this.WIDTH = ww;
    this.HEIGHT = wh;
    this.DATA_SIZE = WIDTH * HEIGHT;
    this.DATA = new ChunkCSOA(WIDTH, HEIGHT);
    generateChunks();

    // this.NUM_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    // this.POOL = Executors.newFixedThreadPool(NUM_THREADS);
  }

  private void generateChunks() {
    for (int i = 0; i < DATA_SIZE; i++) {
      DATA.setChunkAtIndex(new Chunk(i, this), i);
    }
  }
  //=======================================================================================



  //== SETTERS ==//
  public void incrementTime() { 
    this.time++;
    if (this.time == 0) { this.time = 1; }
  }
  //=======================================================================================

  //== GETTERS ==//
  public int    time()      { return this.time; }
  public int    chunkSize() { return this.CHUNK_SIZE; }
  public int    width()     { return this.WIDTH; }
  public int    height()    { return this.HEIGHT; }
  public int    dataSize()  { return this.DATA_SIZE; }
  public Random getRandom() { return this.RANDOM; }
  //=======================================================================================



  //== DATA ==//
  public Chunk getWorldChunk(int wx, int wy) {
    return DATA.getChunk(wx, wy);
  }

  public Chunk getChunk(int x, int y) {
    if (x < 0 || y < 0) {
      return null;
    }
    int wx = x / CHUNK_SIZE;
    int wy = y / CHUNK_SIZE;
    return getWorldChunk(wx, wy);
  }
  //=======================================================================================
  


  //== COORDS ==//
  public int toGlobalX(int cIndex, int cx) {
    int cPos = cIndex % WIDTH;
    return cPos * CHUNK_SIZE + cx;
  }
  public int toGlobalY(int cIndex, int cy) {
    int cPos = cIndex / WIDTH;
    return cPos * CHUNK_SIZE + cy;
  }
  
  public int toChunkPos(int gPos) {
    return gPos % CHUNK_SIZE;
  }
  //=======================================================================================



  //== CELL LOGIC ==//
  public void setWorldCellIn(int x, int y, int id, int deadline) {
    if (x < 0 || y < 0) { return; }
    int cx = toChunkPos(x);
    int cy = toChunkPos(y);
    Chunk chunk = getChunk(x, y);
    if (chunk != null) {
      chunk.setRawData(cx, cy, Chunk.CELL_ID, id);
      chunk.setRawData(cx, cy, Chunk.CELL_DEADLINE, deadline);
      chunk.setRawData(cx, cy, Chunk.CELL_LAST_MOVED, 0);
      chunk.activateCell(cx, cy);
    }
  }

  public int getChunkData(int x, int y, int pos) {
    Chunk chunk = getChunk(x, y);
    if (chunk == null) { return Chunk.OUT_OF_WORLD; }
    return chunk.getRawData(toChunkPos(x), toChunkPos(y), pos);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  // INTENT
  // private void distributeIntents() {
  //   for (Chunk origin : DATA) {
  //     for (Intent intent : origin.COMMIT_OUTBOX) {
  //       intent.RECEIVER_CHUNK.COMMIT_INBOX.add(intent);
  //     }
  //   }
  // }
  //
  // private void distributeResets() {
  //   for (Chunk origin : DATA) {
  //     for (Intent intent : origin.RESET_OUTBOX) {
  //       intent.SENDER_CHUNK.RESET_INBOX.add(intent);
  //     }
  //   }
  // }

  // private void parallelForEach(Consumer<Chunk> action) throws RuntimeException {
  //   int total = getDataSize();
  //   int batches = Math.min(NUM_THREADS, total);
  //   int batchSize = (total + batches - 1) / batches;
  //
  //   CountDownLatch latch = new CountDownLatch(batches);
  //
  //   for (int b = 0; b < batches; b++) {
  //     int start = b * batchSize;
  //     int end = Math.min(start + batchSize, total);
  //
  //     POOL.execute(() -> {
  //       try {
  //         for (int i = start; i < end; i++) {
  //           action.accept(DATA[i]);
  //         }
  //       } finally {
  //         latch.countDown();
  //       }
  //     });
  //   }
  //
  //   try {
  //     latch.await();
  //   } catch (InterruptedException e) {
  //     Thread.currentThread().interrupt();
  //     throw new RuntimeException("Interrupted, waiting for parallelForEach to finish.", e);
  //   }
  // }

  public void step() {
    for (int i = 0; i < DATA.size(); i++) {
      DATA.getChunkAtIndex(i).process();
    }

    // distributeIntents();

    for (int i = 0; i < DATA.size(); i++) {
      DATA.getChunkAtIndex(i).commit();
    }

    // distributeResets();

    for (int i = 0; i < DATA.size(); i++) {
      DATA.getChunkAtIndex(i).reset();
    }

    incrementTime();
  }

  // public void shutdown() {
  //   if (POOL != null) { POOL.shutdown(); }
  // }
  //=======================================================================================
}
