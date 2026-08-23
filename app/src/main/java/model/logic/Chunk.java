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
  public static final int OUT_OF_WORLD = Integer.MIN_VALUE;

  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  //== FINAL ==//
  private final int INDEX;
  private final World WORLD;
  private final int CHUNK_SIZE;
  private final int BOX_SIZE;

  private final int[] DATA;
  public final DirtyRect RECT; //private after
  final List<Intent> INCOMING = new ArrayList<>();
  final List<Intent> OUTGOING = new ArrayList<>();
  final List<Intent> INCOMING_RESETS = new ArrayList<>();
  final List<Intent> OUTGOING_RESETS = new ArrayList<>();

  //== VARIABLES ==//
  private boolean active = false;

  //== CALL METHOD ==//
  public Chunk(int i, World w) {
    this.INDEX = i;
    this.WORLD = w;
    this.CHUNK_SIZE = WORLD.getChunkSize();
    this.BOX_SIZE = WORLD.getBoxSize();

    this.DATA = new int[CHUNK_SIZE * CHUNK_SIZE * FIELDS];
    this.RECT = new DirtyRect(CHUNK_SIZE);
  }

  //== SETTERS ==//
  public void setActive(boolean value) { this.active = value; }
  //=======================================================================================

  //== GETTERS ==//
  public int getIndex() { return this.INDEX; }
  public boolean isActive() { return this.active; }
  public int getTime() { return WORLD.getTime(); }
  public Random getRandom() { return this.RANDOM; }
  //=======================================================================================



  //== COORDS ==//
  public int getGlobalX(int chunkIndex, int cPos) {
    int wx = chunkIndex % BOX_SIZE;
    return wx * CHUNK_SIZE + cPos;
  }
  public int getGlobalY(int chunkIndex, int cPos) {
    int wy = chunkIndex / BOX_SIZE;
    return wy * CHUNK_SIZE + cPos;
  }

  public int getLocalPos(int gPos) {
    return gPos % CHUNK_SIZE;
  }

  public Chunk getChunkByGlobal(int x, int y) {
    Chunk c = WORLD.getChunk(x, y);
    if (c == null) { throw new IllegalArgumentException("Chunk out of bounds in: {" + x + ", " + y + "}"); }
    return c;
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
  //==============================================

  // PUBLICS
  public void registerActivation(int fromX, int fromY, int toX, int toY) {
    String where = "to";
    if (fromX == toX && fromY == toY) { where = "from"; }
    this.registerIntent(true, where, fromX, fromY, toX, toY, 0, 0, 0, 0);
  }

  public void registerIntent(
      boolean activeOnly, String where,
      int fromX, int fromY, 
      int toX, int toY, 
      int fromId, int fromDl, int toId, int toDl
      )
  {
    if (!where.equals("to") && !where.equals("from")) {
      throw new IllegalArgumentException("Where is incorrecly defined in: {" + where + "}");
    }

    if (!inBounds(getLocalPos(fromX), getLocalPos(fromY))) {
      throw new IllegalArgumentException("Cell out of bounds in: {" + fromX + ", " + fromY + "}");
    }

    Intent intent = new Intent();
    intent.ACTIVATION_ONLY = activeOnly;
    intent.WHERE = where;

    intent.FROM_CHUNK = this;
    intent.FROM_X = fromX;
    intent.FROM_Y = fromY;
    intent.FROM_ID = fromId;
    intent.FROM_DEADLINE = fromDl;

    intent.TO_CHUNK = getChunkByGlobal(toX, toY);
    intent.TO_X = toX;
    intent.TO_Y = toY;
    intent.TO_ID = toId;
    intent.TO_DEADLINE = toDl;

    if (inBounds(getLocalPos(toX), getLocalPos(toY))) {
      INCOMING.add(intent);
    } else {
      OUTGOING.add(intent);
    }
  }

  public int getDataIn(int x, int y, int pos) {
    if (!(pos >= 0 && pos < FIELDS)) { 
      throw new IllegalArgumentException(pos + " is a invalid position in chunk data."); 
    }

    int cx = getLocalPos(x);
    int cy = getLocalPos(y);
    if (inBounds(cx, cy)) {
      return getRawDataPoint(cx, cy, pos);
    }

    Chunk valid = WORLD.getChunk(x, y);
    if (valid == null) { return OUT_OF_WORLD; }
    return valid.getDataIn(x, y, pos);
  }
  //==============================================
  //
  //=======================================================================================



  //== GAME LOGIC ==//
  private void applyIntent(Intent i) {
    switch (i.WHERE) {
      case "to":

        int tcx = getLocalPos(i.TO_X);
        int tcy = getLocalPos(i.TO_Y);
        if (i.ACTIVATION_ONLY) {
          activateCell(tcx, tcy);
          i.WHERE = "from";
          if (i.FROM_CHUNK != this) {
            OUTGOING_RESETS.add(i);
          }
          break;
        }

        int defaultidx = dataIndex(tcx, tcy);
        DATA[defaultidx + CELL_ID] = i.TO_ID;
        DATA[defaultidx + CELL_DEADLINE] = i.TO_DEADLINE;
        DATA[defaultidx + CELL_LAST_MOVED] = (i.TO_ID != 0) ? WORLD.getTime() : 0;
        activateCell(tcx, tcy);

        if (i.FROM_CHUNK == null) { break; }
        i.WHERE = "from";
        if (i.FROM_CHUNK == this) {
          INCOMING_RESETS.add(i);
        } else {
          OUTGOING_RESETS.add(i);
        }

        break;
      case "from":

        int fcx = getLocalPos(i.FROM_X);
        int fcy = getLocalPos(i.FROM_Y);
        if (i.ACTIVATION_ONLY) {
          activateCell(fcx, fcy);
          break;
        }

        int resetidx = dataIndex(fcx, fcy);
        DATA[resetidx + CELL_ID] = i.FROM_ID;
        DATA[resetidx + CELL_DEADLINE] = i.FROM_DEADLINE;
        DATA[resetidx + CELL_LAST_MOVED] = (i.FROM_ID != 0) ? WORLD.getTime() : 0;
        activateCell(fcx, fcy);

        break;
      default:
        throw new IllegalArgumentException("Intent was not initialized corrctly: " + i.WHERE);
    }
  }

  private void stepCell(int cx, int cy) {
    int x = getGlobalX(getIndex(), cx);
    int y = getGlobalY(getIndex(), cy);

    int cID = getRawDataPoint(cx, cy, CELL_ID);
    if (cID == 0) { return; } // air
                              
    int lastMoved = getRawDataPoint(cx, cy, CELL_LAST_MOVED);
    if (lastMoved == WORLD.getTime()) {
      registerActivation(x, y, x, y);
      return; 
    }

    Cell cell = CHolder.get(cID);
    cell.step(this, x, y);
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
    for (Intent intent : INCOMING) {
      if (intent.ACTIVATION_ONLY) {
        applyIntent(intent);
        continue;
      }

      if (intent.TO_CHUNK.getRawDataPoint(getLocalPos(intent.TO_X), getLocalPos(intent.TO_Y), CELL_LAST_MOVED) == getTime()) { continue; }
      applyIntent(intent);
    }
  }

  // void commit() {
  //   for (int i = INCOMING.size() - 1; i > 0; i--) {
  //     int j = getRandom().nextInt(i + 1);
  //     Intent tmp = INCOMING.get(i);
  //     INCOMING.set(i, INCOMING.get(j));
  //     INCOMING.set(j, tmp);
  //
  //     Intent current = INCOMING.get(i);
  //
  //     if (current.ACTIVATION_ONLY) { 
  //       applyIntent(current, false);
  //       continue;
  //     }
  //     if (current.TO_CHUNK.getRawDataPoint(current.TO_CX, current.TO_CY, CELL_LAST_MOVED) == getTime()) { continue; }
  //
  //     applyIntent(current, false);
  //   }
  //
  //   if (!INCOMING.isEmpty()) {
  //     Intent first = INCOMING.get(0);
  //
  //     if (first.ACTIVATION_ONLY) {
  //       applyIntent(first, false);
  //     } else if (first.TO_CHUNK.getRawDataPoint(first.TO_CX, first.TO_CY, CELL_LAST_MOVED) != getTime()) {
  //       applyIntent(first, false);
  //     }
  //   }
  // }

  void applyResets() {
    for (Intent i : INCOMING_RESETS) {
      applyIntent(i);
    }
    INCOMING_RESETS.clear();
    OUTGOING_RESETS.clear();
  }
  //=======================================================================================
}
