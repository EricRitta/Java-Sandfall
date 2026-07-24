package model.logic;
import java.util.Random;

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
  //==============================================================================

  public Chunk(int id, World w) {
    this.ID = id;
    this.WORLD = w;
    this.CHUNK_SIZE = WORLD.getCHUNK_SIZE();
    this.data = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
    this.active = new int[CHUNK_SIZE * CHUNK_SIZE];
  }

  private int index(int cx, int cy) {
    return (cy * CHUNK_SIZE + cx) * FIELDS;
  }

  //== SETTERS ==//
  // basic
  public void setProcessId(int value) { this.processId = value; }
  public void setIsActive(boolean value) { this.isActive = value; }

  // neighbor
  public void setNeighbor(int dx, int dy, Chunk neighbor) {
    NEIGHBORS_GRID[(dy + 1) * NEIGHBOR_GRID_SIZE + (dx + 1)] = neighbor;
  }
  
  // data
  public void setRawCell(int cx, int cy, int value) {
    data[index(cx, cy) + CELL_ID] = value; 
  }

  public void setRawCellDeadline(int cx, int cy, int value) {
    data[index(cx, cy) + CELL_DEADLINE] = value;
  }

  public void setRawCellProcessId(int cx, int cy, int value) {
    if (value == 0 || value == 1) {
      data[index(cx, cy) + CELL_PROCESS_ID] = value;
    } else {
      throw new IllegalArgumentException(
        "setProcessed argument value is different then 0 or 1: (" + value + ")"
      );
    }
  }
  //==============================================================================

  //== GETTERS ==//
  // basic
  public int getProcessId() { return this.processId; }
  public boolean getIsActive() { return this.isActive; }

  // neighbor
  public Chunk getNeighbor(int dx, int dy) {
    return NEIGHBORS_GRID[(dy + 1) * NEIGHBOR_GRID_SIZE + (dx + 1)];
  }

  // data
  public int getRawCell(int cx, int cy) {
    return data[index(cx, cy) + CELL_ID];
  }

  public int getRawCellDeadline(int cx, int cy) {
    return data[index(cx, cy) + CELL_DEADLINE];
  }

  public int getRawCellProcessId(int cx, int cy) {
    return data[index(cx, cy) + CELL_PROCESS_ID];
  }
  //==============================================================================

  // PRIVATES //
  private boolean inBounds(int cx, int cy) {
    return cx >= 0 && cx < CHUNK_SIZE && cy >= 0 && cy < CHUNK_SIZE;
  }
  
  // neighbor
  private int chunkToNeighborGrid(int cpos) {
    if (cpos < 0) { return -1; }
    if (cpos >= CHUNK_SIZE) { return 1; }
    return 0;
  }
  private int translateToNeighbor(int cpos) {
    if (cpos < 0) { return cpos + CHUNK_SIZE; }
    if (cpos >= CHUNK_SIZE) { return cpos - CHUNK_SIZE; }
    return cpos;
  }

  // game logic
  private int stepCell(int i) {
    return i;
  }

  private void shuffleAndProcess() {
    int i = activeCount - 1;
    while (i >= 0) {
      int j = getRandom().nextInt(i + 1);
      int tmp = active[i];
      active[i] = active[j];
      active[j] = tmp;

      int cellIndex = active[i];
      int cellProcessId = stepCell(cellIndex); // method that get the specific cell by Id and steps it, returning processId

      if (processId != cellProcessId) {
        active[i] = active[activeCount - 1];
        activeCount--;
      }

      i--;
    }
  }
  //==============================================================================
  
  //== PUBLICS ==//
  // basic
  public int getTime() {
    return WORLD.getTime();
  }

  public Random getRandom() {
    return WORLD.getRandom();
  }

  // cell logic
  public void setCellIn(int cx, int cy, int id, int deadline, int processId) {
    if (inBounds(cx, cy)) {
      int idx = index(cx, cy);
      data[idx + CELL_ID] = id;
      data[idx + CELL_DEADLINE] = deadline;
      data[idx + CELL_PROCESS_ID] = processId;
      return;
    }
    Chunk neighbor = getNeighbor(chunkToNeighborGrid(cx), chunkToNeighborGrid(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    neighbor.setCellIn(translateToNeighbor(cx), translateToNeighbor(cy), id, deadline, processId);
    // TODO: fallback to world if x or y doubles the neighbor (128+ or -64+)
  }

  public int getCellIn(int cx, int cy) {
    if (inBounds(cx, cy)) {
      return getRawCell(cx, cy);
    } 
    Chunk neighbor = getNeighbor(chunkToNeighborGrid(cx), chunkToNeighborGrid(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    return neighbor.getCellIn(translateToNeighbor(cx), translateToNeighbor(cy));
    // TODO: fallback to world if x or y doubles the neighbor (128+ or -64+)
  }

  // TODO: sleeping method for future
  // public void activateCell(int cx, int cy) {
  //
  // }

  // game logic
  public int step() {
    this.processId ^= 1;
    shuffleAndProcess();
    if (activeCount == 0) {
      this.processId ^= 1;
      this.isActive = false;
      return getProcessId();
    }
    this.isActive = true;
    return getProcessId();
  }
  //==============================================================================
}
