package model.logic;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class World {
  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  private final int NUM_THREADS;
  private final ExecutorService POOL;

  public final Chunk[] DATA; // private after
  private final int CHUNK_SIZE;
  private final int BOX_SIZE;
  private final int DATA_SIZE;

  //== VARIABLES ==//
  private int time = 1;

  //== CALL METHOD ==//
  public World(int cs, int bs) {
    this.BOX_SIZE = bs;
    this.CHUNK_SIZE = cs;
    this.DATA = new Chunk[BOX_SIZE * BOX_SIZE];
    this.DATA_SIZE = BOX_SIZE * BOX_SIZE;

    this.NUM_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    this.POOL = Executors.newFixedThreadPool(NUM_THREADS);
  }

  public void init() {
    generateChunks();
  }
  //=======================================================================================



  //== SETTERS ==//
  public void incrementTime() { 
    this.time++;
    if (this.time == 0) { this.time = 1; }
  }
  //=======================================================================================

  //== GETTERS ==//
  public int getTime() { return time; }
  public int getBoxSize() { return BOX_SIZE; }
  public int getDataSize() { return DATA_SIZE; }
  public int getChunkSize() { return CHUNK_SIZE; }
  public int getWorldWidth() { return CHUNK_SIZE * BOX_SIZE; }
  public int getWorldHeight() { return CHUNK_SIZE * BOX_SIZE; }
  public int getWorldSize() { return getWorldWidth() * getWorldHeight(); }
  public Random getRandom() { return this.RANDOM; }
  //=======================================================================================



  //== DATA ==//
  private int dataIndex(int wx, int wy) {
    return (wy * BOX_SIZE + wx);
  }

  public Chunk getRawChunk(int wx, int wy) {
    if (wx < 0 || wx >= BOX_SIZE || wy < 0 || wy >= BOX_SIZE) {
      return null;
    }
    return DATA[dataIndex(wx, wy)];
  }

  private void generateChunks() {
    for (int i = 0; i < (BOX_SIZE * BOX_SIZE); i++) {
      DATA[i] = new Chunk(i, this);
    }
  }
  //=======================================================================================
  


  //== COORDS ==//
  public int toGlobalX(int cIndex, int cx) {
    int cPos = cIndex % BOX_SIZE;
    return cPos * CHUNK_SIZE + cx;
  }
  public int toGlobalY(int cIndex, int cy) {
    int cPos = cIndex / BOX_SIZE;
    return cPos * CHUNK_SIZE + cy;
  }
  
  public int toChunkPos(int gPos) {
    return gPos % CHUNK_SIZE;
  }
  //=======================================================================================


  //== CELL LOGIC ==//
  public void setWorldCellIn(int gx, int gy, int id, int deadline) {
    if (gx < 0 || gy < 0) { return; }
    int cx = toChunkPos(gx);
    int cy = toChunkPos(gy);
    Chunk chunk = getChunk(gx, gy);
    if (chunk != null) {
      chunk.setRawData(cx, cy, Chunk.CELL_ID, id);
      chunk.setRawData(cx, cy, Chunk.CELL_DEADLINE, deadline);
      chunk.setRawData(cx, cy, Chunk.CELL_LAST_MOVED, 0);
      chunk.activateCell(cx, cy);
    }
  }

  public int getChunkData(int x, int y, int pos) {
    Chunk c = getChunk(x, y);
    if (c == null) { return Chunk.OUT_OF_WORLD; }
    return c.getRawData(toChunkPos(x), toChunkPos(y), pos);
  }

  public Chunk getChunk(int x, int y) {
    if (x < 0 || y < 0) {
      return null;
    }
    int wx = x / CHUNK_SIZE;
    int wy = y / CHUNK_SIZE;
    return getRawChunk(wx, wy);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  // INTENT
  private void distributeIntents() {
    for (Chunk origin : DATA) {
      for (Intent intent : origin.COMMIT_OUTBOX) {
        intent.RECEIVER_CHUNK.COMMIT_INBOX.add(intent);
      }
    }
  }

  private void distributeResets() {
    for (Chunk origin : DATA) {
      for (Intent intent : origin.RESET_OUTBOX) {
        intent.SENDER_CHUNK.RESET_INBOX.add(intent);
      }
    }
  }

  private void parallelForEach(Consumer<Chunk> action) throws RuntimeException {
    int total = getDataSize();
    int batches = Math.min(NUM_THREADS, total);
    int batchSize = (total + batches - 1) / batches;

    CountDownLatch latch = new CountDownLatch(batches);

    for (int b = 0; b < batches; b++) {
      int start = b * batchSize;
      int end = Math.min(start + batchSize, total);

      POOL.execute(() -> {
        try {
          for (int i = start; i < end; i++) {
            action.accept(DATA[i]);
          }
        } finally {
          latch.countDown();
        }
      });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrupted, waiting for parallelForEach to finish.", e);
    }
  }

  public void step() {
    for (Chunk chunk : DATA) {
      chunk.process();
    }

    distributeIntents();

    for (Chunk chunk : DATA) {
      chunk.commit();
    }

    distributeResets();


    for (Chunk chunk : DATA) {
      chunk.applyResets();
    }

    // parallelForEach(Chunk::process); // process phase: process cells position and set incoming and outgoing intentions
    // distributeIntents(); // distribution phase: distrubute outgoing intentions to incoming intentions.
    // parallelForEach(Chunk::commit); // commit phase: commit incoming intentions and register reset intentions.
    // distributeResets();
    // parallelForEach(Chunk::applyResets); // reset phase: commit reset intentions.
    incrementTime();
  }

  public void shutdown() {
    if (POOL != null) { POOL.shutdown(); }
  }
  //=======================================================================================
  
  
  
  // DEBUGGING //
  // private void printAllActiveChunks() {
  //   for (Chunk c : activeChunks) {
  //     if (c == null) { continue; }
  //     System.out.println(c.getIndex());
  //   }
  // }
  //=======================================================================================
}
