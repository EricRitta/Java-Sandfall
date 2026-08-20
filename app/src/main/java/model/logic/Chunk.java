package model.logic;

import model.cells.CHolder;
import model.logic.DirtyRect;
import model.cells.Cell;
import java.util.Random;

public class Chunk {
  //== CONSTANTS ==//
  private final Random random = new Random();
  private static final int NEIGHBOR_GRID_SIZE = 3;
  public static final int OUT_OF_WORLD = Integer.MIN_VALUE;
  public static final int FIELDS = 3;
  public static final int CELL_ID = 0;
  public static final int CELL_DEADLINE = 1;
  public static final int CELL_LAST_MOVED = 2;

  //== "SEMI-CONSTANTS" ==//
  private final int INDEX;
  private final World WORLD;
  private final int CHUNK_SIZE;
  private int PHASE = -1; 
  private Chunk[] NEIGHBORS_GRID = new Chunk[NEIGHBOR_GRID_SIZE * NEIGHBOR_GRID_SIZE]; //private after

  //== VARIABLES ==//
  private boolean active = false;
  
  private int[] data;
  private DirtyRect holdingRect;
  private DirtyRect processRect;

  //== CALL METHOD ==//
  public Chunk(int i, World w) {
    this.INDEX = i;
    this.WORLD = w;
    this.CHUNK_SIZE = WORLD.getChunkSize();

    this.data = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
    this.holdingRect = new DirtyRect(CHUNK_SIZE);
    this.processRect = new DirtyRect(CHUNK_SIZE);
  }

  //== SETTERS ==//
  public void setActive(boolean value) { this.active = value; }
  public void setPhase(int value) { 
    if (this.PHASE > -1) { return; }
    this.PHASE = value; 
  } 
  //=======================================================================================

  //== GETTERS ==//
  public int getIndex() { return this.INDEX; }
  public int getPhase() { return this.PHASE; }
  public boolean isActive() { return this.active; }
  public int getNeighborGridSize() { return NEIGHBOR_GRID_SIZE; }
  public int getTime() { return WORLD.getTime(); }
  public Random getRandom() { return this.random; }
  //=======================================================================================



  //== NEIGHBOR ==//
  private int neighborIndex(int nx, int ny) {
    return (ny * NEIGHBOR_GRID_SIZE + nx);
  }

  void setNeighbor(int nx, int ny, Chunk neighbor) {
    NEIGHBORS_GRID[neighborIndex(nx, ny)] = neighbor;
  }
  public Chunk getNeighbor(int nx, int ny) {
    return NEIGHBORS_GRID[neighborIndex(nx, ny)];
  }
  
  private int chunkPosToNeighborPos(int cpos) {
    if (cpos < 0) { return 0; }
    if (cpos >= CHUNK_SIZE) { return 2; }
    return 1;
  }
  private int translateToNeighbor(int cpos) {
    if (cpos < 0) { return cpos + CHUNK_SIZE; }
    if (cpos >= CHUNK_SIZE) { return cpos - CHUNK_SIZE; }
    return cpos;
  }
  //=======================================================================================



  //== DATA ==//
  private int dataIndex(int cx, int cy) {
    return (cy * CHUNK_SIZE + cx) * FIELDS;
  }
  private boolean inBounds(int cx, int cy) {
    return cx >= 0 && cx < CHUNK_SIZE && cy >= 0 && cy < CHUNK_SIZE;
  }

  public void setRawDataPoint(int cx, int cy, int pos, int value) {
    data[dataIndex(cx, cy) + pos] = value;
  }
  public int getRawDataPoint(int cx, int cy, int pos) {
    return data[dataIndex(cx, cy) + pos];
  }
  //=======================================================================================
  


  //== CELL LOGIC ==//
  // TODO: make this activate neighbors
  public void activateCell(int cx, int cy) {
    if (inBounds(cx, cy)) {
      holdingRect.makeDirty(cx, cy);
      setActive(true);
      return;
    }

    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    neighbor.activateCell(translateToNeighbor(cx), translateToNeighbor(cy));
  }
  private boolean verifyPointPos(int pos) {
    return (pos >= 0 && pos < FIELDS);
  }

  public void setDataPointIn(int cx, int cy, int id, int deadline, int lastMoved) {
    if (lastMoved < 0) {
      throw new IllegalArgumentException("Last Moved argument less than 0.");
    }
    if (inBounds(cx, cy)) {
      int idx = dataIndex(cx, cy);
      data[idx + CELL_ID] = id;
      data[idx + CELL_DEADLINE] = deadline;
      data[idx + CELL_LAST_MOVED] = lastMoved;
      activateCell(cx, cy);
      return;
    }

    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    neighbor.setDataPointIn(translateToNeighbor(cx), translateToNeighbor(cy), id, deadline, lastMoved);
  }
  public void setDataPointIn(int cx, int cy, int id, int deadline) {
    if (inBounds(cx, cy)) {
      int idx = dataIndex(cx, cy);
      data[idx + CELL_ID] = id;
      data[idx + CELL_DEADLINE] = deadline;
      data[idx + CELL_LAST_MOVED] = WORLD.getTime();
      activateCell(cx, cy);
      return;
    }

    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    neighbor.setDataPointIn(translateToNeighbor(cx), translateToNeighbor(cy), id, deadline);
  }
  public void resetDataPointIn(int cx, int cy) {
    setDataPointIn(cx, cy, 0, 0, 0);
  }

  public int getDataPointIn(int cx, int cy, int pos) {
    if (!verifyPointPos(pos)) { 
      throw new IllegalArgumentException(pos + " is a invalid position in chunk data."); 
    }

    if (inBounds(cx, cy)) {
      return getRawDataPoint(cx, cy, pos);
    }

    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { return OUT_OF_WORLD; }
    return neighbor.getDataPointIn(translateToNeighbor(cx), translateToNeighbor(cy), pos);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  private void stepCell(int cx, int cy) {
    int lastMoved = getRawDataPoint(cx, cy, CELL_LAST_MOVED);
    if (lastMoved == WORLD.getTime()) { 
      activateCell(cx, cy);
      return; 
    }

    int cID = getRawDataPoint(cx, cy, CELL_ID);
    if (cID == 0) {  return;  } // air

    Cell cell = CHolder.get(cID);
    cell.step(this, cx, cy);
  }

  private void shuffleAndProcess() {
    processRect.copyFrom(holdingRect);
    holdingRect.clear();
    if (processRect.getIsEmpty()) { return; }

    for (int cy = processRect.getMinCY(); cy <= processRect.getMaxCY(); cy++) {
      for (int cx = processRect.getMinCX(); cx <= processRect.getMaxCX(); cx++) {
        stepCell(cx, cy);
      }
    }
    
    // TODO: Revist when dealing with water
    // boolean reverseCX = getRandom().nextBoolean();
    // boolean reverseCY = getRandom().nextBoolean();
    //
    // for (int dy = processRect.getMinCY(); dy <= processRect.getMaxCY(); dy++) {
    //   int cy = reverseCY ? (processRect.getMaxCY() - (dy - processRect.getMinCY())) : dy;
    //   for (int dx = processRect.getMinCX(); dx <= processRect.getMaxCX(); dx++) {
    //     int cx = reverseCX ? (processRect.getMaxCX() - (dx - processRect.getMinCX())) : dx;
    //
    //     stepCell(cx, cy);
    //   }
    // }
  }

  void step() {
    if (holdingRect.getIsEmpty()) {
      setActive(false);
      return;
    }
    shuffleAndProcess();
  }
  //=======================================================================================
}
