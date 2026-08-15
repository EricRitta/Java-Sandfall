package model.logic;

import util.Config;
import model.cells.CHolder;
import model.logic.DirtyRect;
import model.cells.Cell;
import java.util.Random;

public class Chunk {
  //== CONSTANTS ==//
  private static final int FIELDS = Config.getInt("CHUNK_FIELDS");
  private static final int CELL_ID = Config.getInt("CELL_FIELD");
  private static final int CELL_DEADLINE = Config.getInt("CELL_DEADLINE_FIELD");
  private static final int CELL_SKIP_THIS_FRAME = Config.getInt("CELL_SKIP_THIS_FRAME_FIELD");
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
  //=======================================================================================

  //== GETTERS ==//
  public int getIndex() { return this.INDEX; }
  public boolean getIsActive() { return this.isActive; }
  public int getNeighborGridSize() { return NEIGHBOR_GRID_SIZE; }
  public int getTime() { return WORLD.getTime(); }
  public Random getRandom() { return WORLD.getRandom(); }
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

  private void setRawDataPoint(int cx, int cy, int pos, int value) {
    data[dataIndex(cx, cy) + pos] = value;
  }
  private int getRawDataPoint(int cx, int cy, int pos) {
    return data[dataIndex(cx, cy) + pos];
  }
  //=======================================================================================
  


  //== CELL LOGIC ==//
  public void activateCell(int cx, int cy) {
    holdingRect.makeDirty(cx, cy);
    setIsActive(true);
  }
  private boolean verifyPointPos(int pos) {
    return (pos >= 0 && pos < FIELDS);
  }

  public void setDataPointIn(int cx, int cy, int id, int deadline, int skipThisFrame) {
    if (skipThisFrame != 1 || skipThisFrame != 0) {  
      throw new IllegalArgumentException("SkipThisFrame was different than 0 or 1 in setDataPointIn."); 
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
    neighbor.setDataPointIn(translateToNeighbor(cx), translateToNeighbor(cy), id, deadline, skipThisFrame);
  }
  public void setDataPointIn(int cx, int cy, int id, int deadline) {
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
    if (neighbor == null) { return Config.getInt("OUT_OF_WORLD"); }
    return neighbor.getDataPointIn(translateToNeighbor(cx), translateToNeighbor(cy), pos);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
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

  boolean step() {
    shuffleAndProcess();
    if (processRect.getIsEmpty()) {
      setIsActive(false);
      return getIsActive();
    }
    setIsActive(true);
    return getIsActive();
  }
  //=======================================================================================
}
