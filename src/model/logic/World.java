package model.logic;

public class World {
  private final int FIELDS = 2;
  private final int CHUNK = 0;
  private final int PROCESSED = 1;

  private int CHUNK_SIZE = 64;
  private int WORLD_SIZE = 8; // 512 x 512 celulas, ou seja 250k+ celulas
  private int time = 0;

  private int[] data;

  public World(int cs, int ws) {
    this.CHUNK_SIZE = cs;
    this.WORLD_SIZE = ws;
    data = new int[WORLD_SIZE * WORLD_SIZE * FIELDS];
  }

  private int index(int x, int y) {
    return (y * CHUNK_SIZE + x) * FIELDS;
  }

  // SETTERS //
  public void setId(int x, int y, int value) { 
    data[index(x, y) + ID] = value; 
  }

  // GETTERS //
}
