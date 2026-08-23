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

  private final Chunk[] DATA;
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
    this.BATCH_SIZE = 
  }

  public void init() {
    generateChunks();
    linkAllNeighbors();
  }

  //== NEIGHBORS ==//
  public Chunk getChunk(int wx, int wy) {
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

  private void linkNeighbors(int wx, int wy) {
    Chunk chunk = DATA[dataIndex(wx, wy)];
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        Chunk neighbor = getChunk(wx + dx, wy + dy);
        chunk.setNeighbor(1 + dx, 1 + dy, neighbor);
      }
    }
  }
  private void linkAllNeighbors() {
    for (int wy = 0; wy < BOX_SIZE; wy++) {
      for (int wx = 0; wx < BOX_SIZE; wx++) {
        linkNeighbors(wx, wy);
      }
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
  //=======================================================================================
  


  //== CHUNK ==//
  public int getChunkPosByGlobalPos(int g) {
    return g % CHUNK_SIZE;
  }
  public Chunk getChunkClassByGlobalPos(int gx, int gy) {
    int wx = gx / CHUNK_SIZE;
    int wy = gy / CHUNK_SIZE;
    return getChunk(wx, wy);
  }

  public void setWorldCellIn(int gx, int gy, int id, int deadline) {
    if (gx < 0 || gy < 0) { return; }
    int cx = getChunkPosByGlobalPos(gx);
    int cy = getChunkPosByGlobalPos(gy);
    Chunk chunk = getChunkClassByGlobalPos(gx, gy);
    if (chunk != null) {
      chunk.setRawDataPoint(cx, cy, Chunk.CELL_ID, id);
      chunk.setRawDataPoint(cx, cy, Chunk.CELL_DEADLINE, deadline);
      chunk.setRawDataPoint(cx, cy, Chunk.CELL_LAST_MOVED, 0);
      chunk.activateCell(cx, cy);
    }
  }

  public int getWorldCellIn(int gx, int gy) {
    if (gx < 0 || gy < 0) { return 0; }
    int cx = getChunkPosByGlobalPos(gx);
    int cy = getChunkPosByGlobalPos(gy);
    Chunk chunk = getChunkClassByGlobalPos(gx, gy);
    int cID = chunk.getDataPointIn(cx, cy, Chunk.CELL_ID);
    return cID;
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  // INTENT
  private void distributeIntents() {
    for (Chunk origin : DATA) {
      for (Intent intent : origin.OUTGOING) {
        intent.TO_CHUNK.INCOMING.add(intent);
      }
    }
  }

  private void parallelForEach(Consumer<Chunk> action) {
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
    parallelForEach(Chunk::process);
    distributeIntents();
    parallelForEach(Chunk::commit);
    parallelForEach(Chunk::applyResets);
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
