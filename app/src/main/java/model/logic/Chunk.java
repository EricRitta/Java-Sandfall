package model.logic;

public class Chunk {
  private final int FIELDS= 3;
  private final int CELL_ID = 0;
  private final int CELL_DEADLINE = 1;
  private final int CELL_PROCESS_ID = 2;

  private int ID;
  private World WORLD;
  private int CHUNK_SIZE;

  private boolean isActive = false;
  private int processId = 0;

  private int[] data;
  private int[] active;
  private int activeCount = 0;

  public Chunk(int id, World w) {
    this.ID = id;
    this.WORLD = w;
    this.CHUNK_SIZE = WORLD.getCHUNK_SIZE();
    this.data = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
    this.active = new int[CHUNK_SIZE * CHUNK_SIZE];
  }



  // IMPORTANT PRIVATES //
  private void inBounds(int cx, int cy) {
    if (cx >= 0 && cx < CHUNK_SIZE && cy >= 0 && cy < CHUNK_SIZE) {
      throw new IllegalArgumentException(
        "Position out of bounds in a " + CHUNK_SIZE + "size chunk: (" + cx + ", " + cy + ")" 
      );
    }
  }
  private int index(int cx, int cy) {
    inBounds(cx, cy);
    return (cy * CHUNK_SIZE + cx) * FIELDS;
  }
  //==============================================================================================================


  // SETTERS //
  public void setCellId(int cx, int cy, int value) {
    data[index(cx, cy) + CELL_ID] = value; 
  }

  public void setCellDeadline(int cx, int cy, int value) {
    data[index(cx, cy) + CELL_DEADLINE] = value;
  }

  public void setCellProcessed(int cx, int cy, int value) {
    if (value == 0 || value == 1) {
      data[index(cx, cy) + CELL_PROCESS_ID] = value;
    } else {
      throw new IllegalArgumentException(
        "setProcessed argument value is different then 0 or 1: (" + value + ")"
      );
    }
  }

  public void setProcessId(int value) { this.processId = value; }
  public void setIsActive(boolean value) { this.isActive = value; }
  //==============================================================================================================



  // GETTERS //
  public int getCellId(int cx, int cy) {
    return data[index(cx, cy) + CELL_ID];
  }

  public int getCellDeadline(int cx, int cy) {
    return data[index(cx, cy) + CELL_DEADLINE];
  }

  public int getCellProcessed(int cx, int cy) {
    return data[index(cx, cy) + CELL_PROCESS_ID];
  }

  public int getProcessId() { return this.processId; }
  public boolean getIsActive() { return this.isActive; }
  //==============================================================================================================
  
  

  // PUBLICS //
  public int step(World world) {
    return this.processId;
  }

  public void setCell(int cx, int cy) {
    
  }
  //==============================================================================================================
}
