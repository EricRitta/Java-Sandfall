package model.logic;

public class Chunk {
  private final int FIELDS= 3;
  private final int ID = 0;
  private final int DEADLINE = 1;
  private final int PROCESSED = 2;

  private int CHUNK_SIZE;
  private int[] data;

  public Chunk(int cs) {
    this.CHUNK_SIZE = cs;
    data = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
  }

  private boolean inBounds(int x, int y) {
    if (x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE) {
      throw new IllegalArgumentException(
        "Position out of bounds in a " + CHUNK_SIZE + "size chunk: (" + x + ", " + y + ")" 
      );
    }
  }
  private int index(int x, int y) {
    inBounds(x, y);
    return (y * CHUNK_SIZE + x) * FIELDS;
  }

  // SETTERS //
  public void setId(int x, int y, int value) {
    data[index(x, y) + ID] = value; 
  }

  public void setDeadline(int x, int y, int value) {
    data[index(x, y) + DEADLINE] = value;
  }

  public void setProcessed(int x, int y, int value) {
    if (value == 0 || value == 1) {
      data[index(x, y) + PROCESSED] = value;
    } else {
      throw new IllegalArgumentException(
        "setProcessed argument value is different then 0 or 1: (" + value + ")"
      );
    }
  }

  // GETTERS //
  public int getId(int x, int y) {
    return data[index(x, y) + ID];
  }

  public int getDeadline(int x, int y) {
    return data[index(x, y) + DEADLINE];
  }

  public int getProcessed(int x, int y) {
    return data[index(x, y) + PROCESSED];
  }
}
