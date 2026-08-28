package model.universe;

// JAVA
import java.util.Random;

// PROJECT
import util.ParallelProcessor;
import model.universe.util.ChunkCSOA;

public class World {
  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  private final int CHUNK_SIZE;
  private final int WIDTH;
  private final int HEIGHT;
  public final ChunkCSOA DATA; // private after

  //== VARIABLES ==//
  private int time = 1;

  //== CALL METHOD ==//
  public World(int cs, int ww, int wh) {
    this.CHUNK_SIZE = cs;
    this.WIDTH = ww;
    this.HEIGHT = wh;
    this.DATA = new ChunkCSOA(WIDTH, HEIGHT);
    generateChunks();
  }

  private void generateChunks() {
    for (int i = 0; i < DATA.size(); i++) {
      DATA.setChunk(new Chunk(i, this), i);
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
  public int    dataSize()  { return this.DATA.size(); }
  public Random getRandom() { return this.RANDOM; }
  //=======================================================================================



  //== DATA ==//
  public Chunk getWorldChunk(int wx, int wy) {
    return DATA.getChunk(DATA.getIndex(wx, wy));
  }

  public Chunk getChunk(int x, int y) {
    if (x < 0 || y < 0 || x >= (width() * chunkSize()) || y >= (height() * chunkSize())) {
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
      chunk.setCell(cx, cy, id, deadline, 0);
    }
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  public void step() {
    ParallelProcessor.forEach(DATA.get(), Chunk::process);
    ParallelProcessor.forEach(DATA.get(), Chunk::commit);
    ParallelProcessor.forEach(DATA.get(), Chunk::reset);
    incrementTime();
  }

  public void shutdown() {
    ParallelProcessor.shutdown();
  }
  //=======================================================================================
}
