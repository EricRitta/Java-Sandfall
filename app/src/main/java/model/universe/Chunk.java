package model.universe;

// JAVA
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

// PROJECT
import model.util.CellCSOA;
import model.universe.DirtyRect;
import model.universe.Intent;

import settings.CellTypes;
import model.cells.CHolder;
import model.cells.Cell;

public class Chunk {
  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  private final int INDEX;
  private final World WORLD;
  private final int SIZE;

  private final CellCSOA DATA;
  private final DirtyRect RECT;
  
  final List<Intent> COMMIT_BOX = new ArrayList<>();
  final List<Intent> RESET_BOX = new ArrayList<>();

  //== VARIABLES ==//
  private boolean active = false;

  //== CALL METHOD ==//
  public Chunk(int i, World w) {
    this.INDEX = i;
    this.WORLD = w;
    this.SIZE = WORLD.chunkSize();
    this.DATA = new CellCSOA(SIZE, SIZE);
    this.RECT = new DirtyRect(SIZE);
  }

  //== SETTERS ==//
  public void setActive(boolean value) { this.active = value; }
  //=======================================================================================

  //== GETTERS ==//
  public int     index()     { return this.INDEX; }
  public int     time()      { return WORLD.time(); }
  public int     size()      { return this.SIZE; }
  public boolean isActive()  { return this.active; }
  public Random  getRandom() { return this.RANDOM; }
  //=======================================================================================


  
  //== DATA ==//
  void setDataId(int cx, int cy, int value) {
    if (!DATA.inBounds(cx, cy)) { return; }
    DATA.setId(value, DATA.getIndex(cx, cy));
  }
  void setDataDeadline(int cx, int cy, int value) {
    if (!DATA.inBounds(cx, cy)) { return; }
    DATA.setDeadline(value, DATA.getIndex(cx, cy));
  }
  void setDataLastUpdatedFrame(int cx, int cy, int value) {
    if (!DATA.inBounds(cx, cy)) { return; }
    DATA.setLastUpdatedFrame(value, DATA.getIndex(cx, cy));
  }

  public int getDataId(int cx, int cy) {
    if (!DATA.inBounds(cx, cy)) { return CellTypes.OUT_OF_WORLD; }
    return DATA.getId(DATA.getIndex(cx, cy));
  }
  public int getDataDeadline(int cx, int cy) {
    if (!DATA.inBounds(cx, cy)) { return CellTypes.OUT_OF_WORLD; }
    return DATA.getDeadline(DATA.getIndex(cx, cy));
  }
  public int getDataLastUpdatedFrame(int cx, int cy) {
    if (!DATA.inBounds(cx, cy)) { return CellTypes.OUT_OF_WORLD; }
    return DATA.getLastUpdatedFrame(DATA.getIndex(cx, cy));
  }
  //=======================================================================================
  


  //== COORDS ==//
  public int toGlobalX(int cx) {
    return WORLD.toGlobalX(index(), cx);
  }
  public int toGlobalY(int cy) {
    return WORLD.toGlobalY(index(), cy);
  }

  public int toChunkX(int x) {
    return WORLD.toChunkPos(x);
  }
  public int toChunkY(int y) {
    return WORLD.toChunkPos(y);
  }
  //=======================================================================================



  //== CELL LOGIC ==//
  // PACKAGE-PRIVATES
  void activateCell(int cx, int cy) {
    RECT.makeDirty(cx, cy);
    setActive(true);
  }
  //==============================================

  // REGISTRATION
  public void registerPing(int senderX, int senderY, int receiverX, int receiverY) {
    registerIntent(true, senderX, senderY, receiverX, receiverY, 0, 0, 0, 0);
  }

  public void registerIntent(
      boolean activeOnly,
      int senderX, int senderY, 
      int receiverX, int receiverY, 
      int senderId, int senderDl, int receiverId, int receiverDl
      )
  {
    if (!DATA.inBounds(toChunkX(senderX), toChunkY(senderY))) {
      throw new IllegalArgumentException("Cell out of bounds in: {" + senderX + ", " + senderY + "}");
    }

    Intent intent = new Intent();
    intent.ACTIVATION_ONLY = activeOnly;

    intent.SENDER_CHUNK = this;
    intent.SENDER_X = senderX;
    intent.SENDER_Y = senderY;
    intent.SENDER_ID = senderId;
    intent.SENDER_DEADLINE = senderDl;

    intent.RECEIVER_CHUNK = getChunkByGlobal(receiverX, receiverY);
    intent.RECEIVER_X = receiverX;
    intent.RECEIVER_Y = receiverY;
    intent.RECEIVER_ID = receiverId;
    intent.RECEIVER_DEADLINE = receiverDl;

    if (intent.RECEIVER_CHUNK == null) { return; }
    intent.RECEIVER_CHUNK.COMMIT_BOX.add(intent);
  }
  //=======================================================================================

  

  //== GLOBAL ==//
  public void setCell(int x, int y, int id, int dl, int lf) {
    int cx = toChunkX(x);
    int cy = toChunkY(y);
    if (DATA.inBounds(cx, cy)) {
      setDataId(cx, cy, id);
      setDataDeadline(cx, cy, dl);
      setDataLastUpdatedFrame(cx, cy, lf);
    }
  }
  //=======================================================================================
  // GETTING
  public int getDataIn(int cx, int cy, int pos) {
    if (!(pos >= 0 && pos < FIELDS)) { 
      throw new IllegalArgumentException(pos + " is a invalid position in chunk data."); 
    }

    if (!inBounds(cx, cy)) {
      return OUT_OF_WORLD;
    }

    return getRawData(cx, cy, pos);
  }

  public int getChunkData(int x, int y, int pos) {
    return WORLD.getChunkData(x, y, pos);
  }

  public Chunk getChunkByGlobal(int x, int y) {
    Chunk c = WORLD.getChunk(x, y);
    return c;
  }
  //==============================================
  //
  //=======================================================================================



  //== GAME LOGIC ==//
  private void registerReset(Intent intent) {
    if (intent.SENDER_CHUNK == this) {
      RESET_INBOX.add(intent);
    } else {
      RESET_OUTBOX.add(intent);
    }
  }

  private void applyCommitIntent(Intent intent) {
    int receiver_cx = toChunkX(intent.RECEIVER_X);
    int receiver_cy = toChunkY(intent.RECEIVER_Y);
 
    if (intent.ACTIVATION_ONLY) {
      activateCell(receiver_cx, receiver_cy);
      // will activate two times if the same cord.
      if (intent.SENDER_X != intent.RECEIVER_X || intent.SENDER_Y != intent.RECEIVER_Y) {
        registerReset(intent);
      }
      return;
    }

    int defaultidx = dataIndex(receiver_cx, receiver_cy);
    DATA[defaultidx + CELL_ID] = intent.RECEIVER_ID;
    DATA[defaultidx + CELL_DEADLINE] = intent.RECEIVER_DEADLINE;
    DATA[defaultidx + CELL_LAST_MOVED] = (intent.RECEIVER_ID != 0) ? getTime() : 0;
    activateCell(receiver_cx, receiver_cy);

    registerReset(intent);
  }

  private void applyResetIntent(Intent intent) {
    int sender_cx = toChunkX(intent.SENDER_X);
    int sender_cy = toChunkY(intent.SENDER_Y);
    if (intent.ACTIVATION_ONLY) {
      activateCell(sender_cx, sender_cy);
      return;
    }

    int resetidx = dataIndex(sender_cx, sender_cy);
    DATA[resetidx + CELL_ID] = intent.SENDER_ID;
    DATA[resetidx + CELL_DEADLINE] = intent.SENDER_DEADLINE;
    DATA[resetidx + CELL_LAST_MOVED] = (intent.SENDER_ID != 0) ? getTime() : 0;
    activateCell(sender_cx, sender_cy);
  }

  private void stepCell(int cx, int cy) {
    int x = toGlobalX(cx);
    int y = toGlobalY(cy);

    int cID = getRawData(cx, cy, CELL_ID);
    if (cID == 0) { return; } // air
                              
    int lastMoved = getRawData(cx, cy, CELL_LAST_MOVED);
    if (lastMoved == WORLD.getTime()) {
      registerActivation(x, y, x, y);
      return; 
    }

    Cell cell = CHolder.get(cID);
    cell.step(this, x, y);
  }

  // REAL CALLERS
  void process() {
    COMMIT_INBOX.clear();
    RESET_INBOX.clear();

    COMMIT_OUTBOX.clear();
    RESET_OUTBOX.clear();

    if (RECT.getIsEmpty()) { 
      setActive(false); 
      return; 
    }

    for (int cy = RECT.getMinCY(); cy <= RECT.getMaxCY(); cy++) {
      for (int cx = RECT.getMinCX(); cx <= RECT.getMaxCX(); cx++) {
        stepCell(cx, cy);
      }
    }

    // boolean reverseCX = getRandom().nextBoolean();
    // boolean reverseCY = getRandom().nextBoolean();
    //
    // for (int dy = RECT.getMinCY(); dy <= RECT.getMaxCY(); dy++) {
    //   int cy = reverseCY ? (RECT.getMaxCY() - (dy - RECT.getMinCY())) : dy;
    //   for (int dx = RECT.getMinCX(); dx <= RECT.getMaxCX(); dx++) {
    //     int cx = reverseCX ? (RECT.getMaxCX() - (dx - RECT.getMinCX())) : dx;
    //
    //     stepCell(cx, cy);
    //   }
    // }

    RECT.clear();
  }

  void commit() {

    for (int i = COMMIT_INBOX.size() - 1; i > 0; i--) {
      int j = getRandom().nextInt(i + 1);
      Intent tmp = COMMIT_INBOX.get(i);
      COMMIT_INBOX.set(i, COMMIT_INBOX.get(j));
      COMMIT_INBOX.set(j, tmp);

      Intent current = COMMIT_INBOX.get(i);

      if (current.ACTIVATION_ONLY) {
        applyCommitIntent(current);
        continue;
      }
      if (getRawData(toChunkX(current.RECEIVER_X), toChunkY(current.RECEIVER_Y), CELL_LAST_MOVED) == getTime()) { continue; }

      applyCommitIntent(current);
    }

    if (!COMMIT_INBOX.isEmpty()) {
      Intent first = COMMIT_INBOX.get(0);

      if (first.ACTIVATION_ONLY) {
        applyCommitIntent(first);

      } else if (getRawData(toChunkX(first.RECEIVER_X), toChunkY(first.RECEIVER_Y), CELL_LAST_MOVED) == getTime()) {
        applyCommitIntent(first);

      }
    }

  }

  void applyResets() {
    for (Intent intent : RESET_INBOX) {
      applyResetIntent(intent);
    }
  }
  //=======================================================================================
}
