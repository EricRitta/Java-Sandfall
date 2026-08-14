package model.logic;

import util.Config;
import model.cells.CHolder;
import model.logic.DirtyRect;
import model.cells.Cell;
import java.util.Random;

public class Chunk {
  //== CONSTANTS ==//
  private static final int FIELDS = 3;
  private static final int CELL_ID = 0;
  private static final int CELL_DEADLINE = 1;
  private static final int CELL_SKIP_THIS_FRAME = 2;
  private static final int NEIGHBOR_GRID_SIZE = 3;

  //== "SEMI-CONSTANTS" ==//
  private int INDEX;
  private World WORLD;
  private int CHUNK_SIZE;
  private Chunk[] NEIGHBORS_GRID = new Chunk[NEIGHBOR_GRID_SIZE * NEIGHBOR_GRID_SIZE]; //private after

  //== VARIABLES ==//
  private boolean isActive = false;
  
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
  public void setIsActive(boolean value) { this.isActive = value; }
  void setNeighbor(int nx, int ny, Chunk neighbor) {
    NEIGHBORS_GRID[neighborIndex(nx, ny)] = neighbor;
  }

  public void setRawDataPoint(int cx, int cy, int pos, int value) {
    data[dataIndex(cx, cy) + pos] = value;
  }

  //== GETTERS ==//
  public int getIndex() { return this.INDEX; }
  public int getNeighborGridSize() { return NEIGHBOR_GRID_SIZE; }
  public boolean getIsActive() { return this.isActive; }

  public int getRawDataPoint(int cx, int cy, int pos) {
    return data[dataIndex(cx, cy) + pos];
  }

  public Chunk getNeighbor(int nx, int ny) {
    return NEIGHBORS_GRID[neighborIndex(nx, ny)];
  }

  //== PRIVATES ==//
  private int dataIndex(int cx, int cy) {
    return (cy * CHUNK_SIZE + cx) * FIELDS;
  }
  private boolean inBounds(int cx, int cy) {
    return cx >= 0 && cx < CHUNK_SIZE && cy >= 0 && cy < CHUNK_SIZE;
  }
  
  // neighbor
  private int neighborIndex(int nx, int ny) {
    return (ny * NEIGHBOR_GRID_SIZE + nx);
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

  // game logic
  private void stepCell(int cx, int cy) {
    int skipThisFrame = getRawDataPoint(cx, cy, CELL_SKIP_THIS_FRAME);
    if (skipThisFrame >= 1) {
      setRawDataPoint(cx, cy, CELL_SKIP_THIS_FRAME, 0);
      return;
    }
    Cell cell = CHolder.get(getRawDataPoint(cx, cy, CELL_ID));
    cell.step(this, cx, cy);
  }

  private void shuffleAndProcess() {
    processRect.copyFrom(holdingRect);
    holdingRect.clear();
    if (processRect.getIsEmpty()) { return; }

    boolean reverseCX = getRandom().nextBoolean();
    boolean reverseCY = getRandom().nextBoolean();

    for (int dy = processRect.getMinCY(); dy <= processRect.getMaxCY(); dy++) {
      int cy = reverseCY ? (processRect.getMaxCY() - (dy - processRect.getMinCY())) : dy;
      for (int dx = processRect.getMinCX(); dx <= processRect.getMaxCX(); dx++) {
        int cx = reverseCX ? (processRect.getMaxCX() - (dx - processRect.getMinCX())) : dx;
        
        stepCell(cx, cy);
      }
    }
  }
  
  //== PUBLICS ==//
  // basic
  public int getTime() { return WORLD.getTime(); }
  public Random getRandom() { return WORLD.getRandom(); }

  // cell logic
  public void setCellIn(int cx, int cy, int id, int deadline, int skipThisFrame) {
    if (skipThisFrame != 1 || skipThisFrame != 0) {  
      throw new IllegalArgumentException("SkipThisFrame was different than 0 or 1 in setCellIn."); 
    }
    if (inBounds(cx, cy)) {
      int idx = dataIndex(cx, cy);
      data[idx + CELL_ID] = id;
      data[idx + CELL_DEADLINE] = deadline;
      data[idx + CELL_SKIP_THIS_FRAME] = skipThisFrame;
      activateCell(cx, cy);
      return;
    }
    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    neighbor.setCellIn(translateToNeighbor(cx), translateToNeighbor(cy), id, deadline, skipThisFrame);
  }
  public void setCellIn(int cx, int cy, int id, int deadline) {
    if (inBounds(cx, cy)) {
      int idx = dataIndex(cx, cy);
      data[idx + CELL_ID] = id;
      data[idx + CELL_DEADLINE] = deadline;
      data[idx + CELL_SKIP_THIS_FRAME] = 1;
      activateCell(cx, cy);
      return;
    }
    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    neighbor.setCellIn(translateToNeighbor(cx), translateToNeighbor(cy), id, deadline);
  }

  public void activateCell(int cx, int cy) {
    holdingRect.makeDirty(cx, cy);
    setIsActive(true);
  }

  public int getCellIn(int cx, int cy) {
    if (inBounds(cx, cy)) {
      return getRawDataPoint(cx, cy, CELL_ID);
    } 
    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { return Config.getInt("OUT_OF_WORLD"); }
    return neighbor.getCellIn(translateToNeighbor(cx), translateToNeighbor(cy));
  }
  public int getCellDeadlineIn(int cx, int cy) {
    if (inBounds(cx, cy)) {
      return getRawDataPoint(cx, cy, CELL_DEADLINE);
    } 
    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { throw new IllegalArgumentException("error"); }
    return neighbor.getCellDeadlineIn(translateToNeighbor(cx), translateToNeighbor(cy));
  }
  // public int getCellSkipThisFrameIn(int cx, int cy) {
  //   if (inBounds(cx, cy)) {
  //     return getRawDataPoint(cx, cy, CELL_SKIP_THIS_FRAME);
  //   } 
  //   Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
  //   if (neighbor == null) { throw new IllegalArgumentException("erro kkk"); }
  //   return neighbor.getCellSkipThisFrameIn(translateToNeighbor(cx), translateToNeighbor(cy));
  // }

  // game logic
  boolean step() {
    shuffleAndProcess();
    if (processRect.getIsEmpty()) {
      setIsActive(false);
      return getIsActive();
    }
    setIsActive(true);
    return getIsActive();
  }
  //==============================================================================
}
