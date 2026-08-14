package model.logic;
import java.util.Random;

public class World {
  //== CONSTANTS ==//
  private final Random random = new Random();

  //== "SEMI-CONSTANTS" ==//
  private int CHUNK_SIZE;
  private int BOX_SIZE;

  //== VARIABLES ==//
  private int time = 0;
  private Chunk[] data;

  //== CALL METHODS ==//
  public World(int cs, int bs) {
    this.CHUNK_SIZE = cs;
    this.BOX_SIZE = bs;
    data = new Chunk[BOX_SIZE * BOX_SIZE];
    generateChunks();
    linkAllNeighbors();
  }

  private void generateChunks() {
    for (int i = 0; i < (BOX_SIZE * BOX_SIZE); i++) {
      data[i] = new Chunk(i, this);
    }
  }
  private void linkAllNeighbors() {
    for (int wy = 0; wy < BOX_SIZE; wy++) {
      for (int wx = 0; wx < BOX_SIZE; wx++) {
        linkNeighbor(wx, wy);
      }
    }
  }
  private void linkNeighbor(int wx, int wy) {
    Chunk chunk = data[dataIndex(wx, wy)];
    for (int dy = -1; dy <= 1; dy++) {
      for (int dx = -1; dx <= 1; dx++) {
        Chunk neighbor = getChunkOrNull(wx + dx, wy + dy);
        chunk.setNeighbor(1 + dx, 1 + dy, neighbor);
      }
    }
  }
  public Chunk getChunkOrNull(int wx, int wy) {
    if (wx < 0 || wx >= BOX_SIZE || wy < 0 || wy >= BOX_SIZE) {
      return null;
    }
    return data[dataIndex(wx, wy)];
  }

  //== SETTERS ==//
  public void incrementTime() { time++; }

  //== GETTERS ==//
  public int getTime() { return time; }
  public int getBoxSize() { return BOX_SIZE; }
  public int getChunkSize() { return CHUNK_SIZE; }
  public int getFullWorldSize() { return CHUNK_SIZE * BOX_SIZE * BOX_SIZE; }
  public Random getRandom() { return this.random; }

  //== PRIVATES ==//
  private int dataIndex(int wx, int wy) {
    return (wy * BOX_SIZE + wx);
  }

  //== PUBLICS ==//
}
