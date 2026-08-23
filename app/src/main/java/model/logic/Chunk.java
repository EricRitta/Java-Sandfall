package model.logic;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import model.logic.DirtyRect;
import model.logic.Intent;

import model.cells.CHolder;
import model.cells.Cell;

public class Chunk {
  //== STATIC CONSTANTS ==//
  public static final int FIELDS = 3;
  public static final int CELL_ID = 0;
  public static final int CELL_DEADLINE = 1;
  public static final int CELL_LAST_MOVED = 2;
  public static final int NEIGHBOR_GRID_SIZE = 3;
  public static final int OUT_OF_WORLD = Integer.MIN_VALUE;

  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  //== FINAL ==//
  private final int INDEX;
  private final World WORLD;
  private final int CHUNK_SIZE;
  private final Chunk[] NEIGHBORS_GRID = new Chunk[NEIGHBOR_GRID_SIZE * NEIGHBOR_GRID_SIZE];

  private final int[] DATA;
  private final DirtyRect RECT;
  final List<Intent> INCOMING = new ArrayList<>();
  final List<Intent> OUTGOING = new ArrayList<>();
  final List<Intent> PENDING_RESETS = new ArrayList<>();

  //== VARIABLES ==//
  private boolean active = false;

  //== CALL METHOD ==//
  public Chunk(int i, World w) {
    this.INDEX = i;
    this.WORLD = w;
    this.CHUNK_SIZE = WORLD.getChunkSize();

    this.DATA = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
    this.RECT = new DirtyRect(CHUNK_SIZE);
  }

  //== SETTERS ==//
  public void setActive(boolean value) { this.active = value; }
  //=======================================================================================

  //== GETTERS ==//
  public int getIndex() { return this.INDEX; }
  public boolean isActive() { return this.active; }
  public int getNeighborGridSize() { return NEIGHBOR_GRID_SIZE; }
  public int getTime() { return WORLD.getTime(); }
  public Random getRandom() { return this.RANDOM; }
  //=======================================================================================



  //== NEIGHBOR ==//
  private int neighborIndex(int nx, int ny) {
    return (ny * NEIGHBOR_GRID_SIZE + nx);
  }

  void setNeighbor(int nx, int ny, Chunk neighbor) {
    NEIGHBORS_GRID[neighborIndex(nx, ny)] = neighbor;
  }
  Chunk getNeighbor(int nx, int ny) {
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

  private Chunk getAvailableNeighbor(int cx, int cy) {
    int nx = chunkPosToNeighborPos(cx);
    int ny = chunkPosToNeighborPos(cy);
    Chunk n = getNeighbor(nx, ny);
    if (n == null) { throw new IllegalArgumentException("Cell out of bounds completely in: " + cx + ", " + cy + "."); }
    return n;
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
    DATA[dataIndex(cx, cy) + pos] = value;
  }
  public int getRawDataPoint(int cx, int cy, int pos) {
    return DATA[dataIndex(cx, cy) + pos];
  }
  //=======================================================================================
  


  //== CELL LOGIC ==//
  // PACKAGE-PRIVATES
  void activateCell(int cx, int cy) {
    RECT.makeDirty(cx, cy);
    setActive(true);
  }

  void registerResetIntent(Intent i) {
    synchronized (PENDING_RESETS) {
      PENDING_RESETS.add(i);
    }
  }
  void registerIntent(Intent intent, int toCX, int toCY) {
    if (inBounds(toCX, toCY)) {
      intent.TO_CHUNK = this;
      intent.TO_CX = toCX;
      intent.TO_CY = toCY;
      return;
    }

    Chunk neighbor = getAvailableNeighbor(toCX, toCY);
    neighbor.registerIntent(intent, translateToNeighbor(toCX), translateToNeighbor(toCY));
  }
  //==============================================

  // PUBLICS
  public void registerActivation(int cx, int cy) {
    Intent intent = new Intent();
    intent.ACTIVATION_ONLY = true;
    intent.TO_CX = cx;
    intent.TO_CY = cy;
    INCOMING.add(intent);
  }

  public void registerIntent(
      int fromCX, int fromCY, 
      int toCX, int toCY, 
      int fromId, int fromDl, int toId, int toDl
      )
  {
    Intent intent = new Intent();
    intent.FROM_CHUNK = this;
    intent.FROM_CX = fromCX;
    intent.FROM_CY = fromCY;
    intent.FROM_ID = fromId;
    intent.FROM_DEADLINE = fromDl;

    intent.TO_ID = toId;
    intent.TO_DEADLINE = toDl;

    if (inBounds(toCX, toCY)) {
      intent.TO_CHUNK = this;
      intent.TO_CX = toCX;
      intent.TO_CY = toCY;

      INCOMING.add(intent);
      return;
    }

    Chunk neighbor = getAvailableNeighbor(toCX, toCY);
    neighbor.registerIntent(intent, translateToNeighbor(toCX), translateToNeighbor(toCY));

    if (intent.TO_CHUNK != null) {
      OUTGOING.add(intent);
    }
  }

  public int getDataPointIn(int cx, int cy, int pos) {
    if (!(pos >= 0 && pos < FIELDS)) { 
      throw new IllegalArgumentException(pos + " is a invalid position in chunk data."); 
    }

    if (inBounds(cx, cy)) {
      return getRawDataPoint(cx, cy, pos);
    }

    Chunk neighbor = getNeighbor(chunkPosToNeighborPos(cx), chunkPosToNeighborPos(cy));
    if (neighbor == null) { return OUT_OF_WORLD; }
    return neighbor.getDataPointIn(translateToNeighbor(cx), translateToNeighbor(cy), pos);
  }
  //==============================================
  //
  //=======================================================================================



  //== GAME LOGIC ==//
  private void applyIntent(Intent i, boolean reseting) {
    if (i.ACTIVATION_ONLY) {
      activateCell(i.TO_CX, i.TO_CY);
      return;
    }

    int idxCX = reseting ? i.FROM_CX : i.TO_CX;
    int idxCY = reseting ? i.FROM_CY : i.TO_CY;
    int id = reseting ? i.FROM_ID : i.TO_ID;
    int dl = reseting ? i.FROM_DEADLINE : i.TO_DEADLINE;

    int idx = dataIndex(idxCX, idxCY);
    DATA[idx + CELL_ID] = id;
    DATA[idx + CELL_DEADLINE] = dl;
    DATA[idx + CELL_LAST_MOVED] = (id != 0) ? WORLD.getTime() : 0;
    activateCell(idxCX, idxCY);

    if (i.FROM_CHUNK == null) { return; }
    if (i.FROM_CHUNK == this) {
      i.FROM_CHUNK = null;
      applyIntent(i, true);
      return;
    }

    i.FROM_CHUNK.registerResetIntent(i);
  }

  private void stepCell(int cx, int cy) {
    int cID = getRawDataPoint(cx, cy, CELL_ID);
    if (cID == 0) { return; } // air
                              
    int lastMoved = getRawDataPoint(cx, cy, CELL_LAST_MOVED);
    if (lastMoved == WORLD.getTime()) {
      registerActivation(cx, cy);
      return; 
    }

    Cell cell = CHolder.get(cID);
    cell.step(this, cx, cy);
  }

  // REAL CALLERS
  void process() {
    INCOMING.clear();
    OUTGOING.clear();
    if (RECT.getIsEmpty()) { 
      setActive(false); 
      return; 
    }

    boolean reverseCX = getRandom().nextBoolean();
    boolean reverseCY = getRandom().nextBoolean();

    for (int dy = RECT.getMinCY(); dy <= RECT.getMaxCY(); dy++) {
      int cy = reverseCY ? (RECT.getMaxCY() - (dy - RECT.getMinCY())) : dy;
      for (int dx = RECT.getMinCX(); dx <= RECT.getMaxCX(); dx++) {
        int cx = reverseCX ? (RECT.getMaxCX() - (dx - RECT.getMinCX())) : dx;

        stepCell(cx, cy);
      }
    }

    RECT.clear();
  }

  void commit() {
    for (Intent i : INCOMING) {
      if (i.ACTIVATION_ONLY) { applyIntent(i, false); continue; }
      if (i.TO_CHUNK.getRawDataPoint(i.TO_CX, i.TO_CY, CELL_LAST_MOVED) == getTime()) { continue; }
      applyIntent(i, false);
    }
  }
    
  void applyResets() {
    for (Intent i : PENDING_RESETS) {
      applyIntent(i, true);
    }
    PENDING_RESETS.clear();
  }
  //=======================================================================================
}
