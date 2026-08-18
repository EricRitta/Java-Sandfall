package model.logic;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class World {
  //== CONSTANTS ==//
  private final Random random = new Random();

  //== "SEMI-CONSTANTS" ==//
  private ExecutorService POOL;
  private int NUM_THREADS;
  private int CHUNK_SIZE;
  private int BOX_SIZE;

  //== VARIABLES ==//
  private int time = 1;
  private Chunk[] data;

  @SuppressWarnings("unchecked")
  private List<Chunk>[] phaseChunks= new List[4];

  //== CALL METHOD ==//
  public World(int cs, int bs) {
    this.CHUNK_SIZE = cs;
    this.BOX_SIZE = bs;
    data = new Chunk[BOX_SIZE * BOX_SIZE];
  }

  public void init() {
    generateChunks();
    linkAllNeighbors();

    this.NUM_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    POOL = Executors.newFixedThreadPool(NUM_THREADS);

    buildPhaseChunks();
  }

  private void buildPhaseChunks() {
    for (int i = 0; i < 4; i++) {
      phaseChunks[i] = new ArrayList<>(); 
    }

    for (int wy = 0; wy < BOX_SIZE; wy++) {
      for (int wx = 0; wx < BOX_SIZE; wx++) {
        int phase = (wx % 2) + (wy % 2) * 2;
        phaseChunks[phase].add(getChunk(wx, wy));
      }
    }
  }

  //== NEIGHBORS ==//
  public Chunk getChunk(int wx, int wy) {
    if (wx < 0 || wx >= BOX_SIZE || wy < 0 || wy >= BOX_SIZE) {
      return null;
    }
    return data[dataIndex(wx, wy)];
  }

  private void generateChunks() {
    for (int i = 0; i < (BOX_SIZE * BOX_SIZE); i++) {
      data[i] = new Chunk(i, this);
    }
  }

  private void linkNeighbors(int wx, int wy) {
    Chunk chunk = data[dataIndex(wx, wy)];
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
  public void incrementTime() { time++; }
  //=======================================================================================

  //== GETTERS ==//
  public int getTime() { return time; }
  public int getBoxSize() { return BOX_SIZE; }
  public int getChunkSize() { return CHUNK_SIZE; }
  public int getWorldWidth() { return CHUNK_SIZE * BOX_SIZE; }
  public int getWorldHeight() { return CHUNK_SIZE * BOX_SIZE; }
  public int getWorldSize() { return getWorldWidth() * getWorldHeight(); }
  public Random getRandom() { return this.random; }
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
    if (chunk != null) { chunk.setDataPointIn(cx, cy, id, deadline, 0); }
  }

  public int getWorldCellIn(int gx, int gy) {
    if (gx < 0 || gy < 0) { return 0; }
    int cx = getChunkPosByGlobalPos(gx);
    int cy = getChunkPosByGlobalPos(gy);
    Chunk chunk = getChunkClassByGlobalPos(gx, gy);
    int cID = chunk.getDataPointIn(cx, cy, Chunk.CELL_ID);
    return cID;
    //return CHolder.get(cID);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  private void poolExecute(CountDownLatch l, int start, int end, List<Chunk> chunks) {
    POOL.execute(() -> {
      try {
        for (int i = start; i < end; i++) {
          Chunk c = chunks.get(i);
          if (c.getIsActive()) {
            c.step();
            System.out.println(Thread.currentThread().getName() + " processando chunk " + i);
          }
        }
      } finally {
        l.countDown();
      }
    });
  }
  
  private void processPhase(int phase) {
    List<Chunk> chunks = phaseChunks[phase];
    int total = chunks.size();
    if (total == 0) { return; }
 
    int batches = Math.min(NUM_THREADS, total);
    int batchSize = (total + batches - 1) / batches;
 
    CountDownLatch latch = new CountDownLatch(batches);
 
    for (int b = 0; b < batches; b++) {
      final int start = b * batchSize;
      final int end = Math.min(start + batchSize, total);
      poolExecute(latch, start, end, chunks);
    }
 
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Interrompido esperando fase " + phase + " terminar.", e);
    }
  }

  public void step() {
    for (int phase = 0; phase < 4; phase++) {
      processPhase(phase);
    }

    incrementTime();
  }

  public void shutdown() {
    if (POOL != null) { POOL.shutdown(); }
  }
  //=======================================================================================
}
