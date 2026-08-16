package model.logic;
import java.util.Random;

import util.Config;

public class World {
  //== CONSTANTS ==//
  private final Random random = new Random();

  //== "SEMI-CONSTANTS" ==//
  private int CHUNK_SIZE;
  private int BOX_SIZE;

  //== VARIABLES ==//
  private int time = 0;
  private Chunk[] data;

  //== CALL METHOD ==//
  public World(int cs, int bs) {
    this.CHUNK_SIZE = cs;
    this.BOX_SIZE = bs;
    data = new Chunk[BOX_SIZE * BOX_SIZE];
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
    int cID = chunk.getDataPointIn(cx, cy, Config.getInt("CELL_FIELD"));
    return cID;
    //return CHolder.get(cID);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  private void processChunk(int wx, int wy) {
    Chunk c = getChunk(wx, wy);
    if (c.getIsActive()) {
      c.step();
    }
  } 

  public void step() {
    for (int wy = 0; wy < BOX_SIZE; wy++) {
      for (int wx = 0; wx < BOX_SIZE; wx++) {
        processChunk(wx, wy);
      }
    }
  }
  //=======================================================================================
}
