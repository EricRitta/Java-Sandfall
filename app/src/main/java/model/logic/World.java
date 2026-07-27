package model.logic;
import java.util.Random;

public class World {
  public static final int OUT_OF_WORLD = Integer.MIN_VALUE;

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
    for (int i = 0; i < BOX_SIZE; i++) {
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
    for (int dy = -1; dy < 1; dy++) {
      for (int dx = -1; dx < 1; dx++) {
        Chunk neighbor = getChunkOrNull(wx + dx, wy + dy);
        chunk.setNeighbor(dx, dy, neighbor);
      }
    }
  }
  private Chunk getChunkOrNull(int wx, int wy) {
    if (wx < 0 || wx >= BOX_SIZE || wy < 0 || wy >= BOX_SIZE) {
      return null;
    }
    return data[dataIndex(wx, wy)];
  }

  //== SETTERS ==//
  public void incrementTime() { time++; }

  //== GETTERS ==//
  public int getWorldSize() { return CHUNK_SIZE * BOX_SIZE; }

  //== PRIVATES ==//
  private int dataIndex(int wx, int wy) {
    return (wy * BOX_SIZE + wx);
  }

  //== PUBLICS ==//

  // IMPORTANT PRIVATES //
  private void inWorldBounds(int x, int y) {
    if (x >= 0 && x < WORLD_SIZE && y >= 0 && y < WORLD_SIZE) {
      throw new IllegalArgumentException(
        "Position out of bounds in a " + WORLD_SIZE + "world size: (" + x + ", " + y + ")" 
      );
    }
  }
  private int worldIndex(int x, int y) {

  }
  private int index(int x, int y) {
    inBounds(x, y);
    return (y * CHUNK_SIZE + x) * FIELDS;
  }
  //==============================================================================================================


  void getWorldIndexByCord(int x, int y) {
      
  }


  // SETTERS //
  public void setId(int x, int y, int value) {

    data[index(x, y) + ID] = value; 
  }
  //==============================================================================================================



  // GETTERS //
  public int getTime() { return this.time; }
  public int getChunkSize() { return this.CHUNK_SIZE; }
  public Random getRandom() { return this.random; }
  //==============================================================================================================
}
