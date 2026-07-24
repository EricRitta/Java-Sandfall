package model.logic;

public class Chunk {
  // CONSTANTS //
  private static final int FIELDS= 3;
  private static final int CELL_ID = 0;
  private static final int CELL_DEADLINE = 1;
  private static final int CELL_PROCESS_ID = 2;

  // "SEMI-CONSTANTS" //
  private int ID;
  private World WORLD;
  private int CHUNK_SIZE;
  private int NEIGHBOR_GRID_SIZE = 9;
  private Chunk[] NEIGHBORS_GRID = new Chunk[NEIGHBOR_GRID_SIZE];

  // VARIABLES //
  private boolean isActive = false;
  private int processId = 0;

  private int[] data;
  private int[] active;
  private int activeCount = 0;
  //==============================================================================================================

  public Chunk(int id, World w) {
    this.ID = id;
    this.WORLD = w;
    this.CHUNK_SIZE = WORLD.getCHUNK_SIZE();
    this.data = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
    this.active = new int[CHUNK_SIZE * CHUNK_SIZE];
  }

  //== SETTERS ==//
  public void setNeighbor(int dx, int dy, Chunk neighbor) {
    NEIGHBORS_GRID[(dy + 1) * NEIGHBOR_GRID_SIZE + (dx + 1)] = neighbor;
  }
  
  public void setCell(int cx, int cy, int value) {
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

  //== GETTERS ==//
  public Chunk getNeighbor(int dx, int dy) {
    return NEIGHBORS_GRID[(dy + 1) * NEIGHBOR_GRID_SIZE + (dx + 1)];
  }

  public int getCell(int cx, int cy) {
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



  // PRIVATES //
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
  
  // se X > 64 e Y normal = right
  // se X < 0 e Y normal = left
  // se X normal e y > 64 = down
  // se X normal e y < 0 = up
  //
  // se X > 64 e Y < 0 = up_right
  // se X > 64 e Y > 64 = down_right
  // se X < 0 e Y < 0 = up_left
  // se X < 0 e Y > 64 = down_left

  //== PUBLICS ==//
  // basic
  public int getTime() {
    return WORLD.getTime();
  }

  // neighbor logic


  public int step(World world) {
    return this.processId;
  }

  public void setCell(int cx, int cy) {
    
  }

  public void activateCell(int cx, int cy) {
    
  }

  // cell logic
  public int getCellIn(int cx, int cy) {
    // bounds
    // true
      // getCellId
    // false
      // neighbor
      // true
        // neighbor.getCell
      // false
        // error, out of bounds 
  }

  //==============================================================================================================
}
