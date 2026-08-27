package model.universe;

// JAVA
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

// PROJECT
import model.universe.util.DirtyRect;
import model.universe.util.Intent;
import model.universe.util.IntentPool;
import model.universe.util.CellCSOA;

import util.CellTypes;
import model.cells.CHolder;
import model.cells.Cell;

public class Chunk {
  //== CONSTANTS ==//
  private final Random RANDOM = new Random();

  private final int INDEX;
  private final World WORLD;
  private final int SIZE;

  private final CellCSOA DATA;
  public final DirtyRect RECT; // private after
  
  final Queue<Intent> COMMIT_BOX = new ConcurrentLinkedQueue<>();
  // final List<Intent> COMMIT_BOX = Collections.synchronizedList(new ArrayList<>());
  final Queue<Intent> RESET_BOX = new ConcurrentLinkedQueue<>();

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
  public int     size()      { return this.SIZE; }
  public boolean isActive()  { return this.active; }
  public Random  getRandom() { return this.RANDOM; }
  //=======================================================================================


  
  //== DATA ==//
  void setDataId(int cx, int cy, int value) {
    if (!DATA.inBounds(cx, cy)) { 
      throw new IllegalArgumentException("Invalid positions: {"+cx+", "+cy+"}");
    }
    DATA.setId(value, DATA.getIndex(cx, cy));
  }
  void setDataDeadline(int cx, int cy, int value) {
    if (!DATA.inBounds(cx, cy)) { 
      throw new IllegalArgumentException("Invalid positions: {"+cx+", "+cy+"}");
    }
    DATA.setDeadline(value, DATA.getIndex(cx, cy));
  }
  void setDataLastUpdatedFrame(int cx, int cy, int value) {
    if (!DATA.inBounds(cx, cy)) { 
      throw new IllegalArgumentException("Invalid positions: {"+cx+", "+cy+"}");
    }
    DATA.setLastUpdatedFrame(value, DATA.getIndex(cx, cy));
  }

  public int getRawDataId(int cx, int cy) {
    if (!DATA.inBounds(cx, cy)) { return CellTypes.OUT_OF_WORLD; }
    return DATA.getId(DATA.getIndex(cx, cy));
  }
  public int getRawDataDeadline(int cx, int cy) {
    if (!DATA.inBounds(cx, cy)) { return CellTypes.OUT_OF_WORLD; }
    return DATA.getDeadline(DATA.getIndex(cx, cy));
  }
  public int getRawDataLastUpdatedFrame(int cx, int cy) {
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
  public void setCell(int cx, int cy, int id, int dl, int lf) {
    if (DATA.inBounds(cx, cy)) {
      setDataId(cx, cy, id);
      setDataDeadline(cx, cy, dl);
      setDataLastUpdatedFrame(cx, cy, lf);
      activateCell(cx, cy);
    }
  }

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

    Intent intent = IntentPool.get();
    intent.ACTIVATION_ONLY = activeOnly;

    intent.SENDER_CHUNK = this;
    intent.SENDER_X = senderX;
    intent.SENDER_Y = senderY;
    intent.SENDER_ID = senderId;
    intent.SENDER_DEADLINE = senderDl;

    intent.RECEIVER_CHUNK = WORLD.getChunk(receiverX, receiverY);
    intent.RECEIVER_X = receiverX;
    intent.RECEIVER_Y = receiverY;
    intent.RECEIVER_ID = receiverId;
    intent.RECEIVER_DEADLINE = receiverDl;

    if (intent.RECEIVER_CHUNK == null) { return; }
    intent.RECEIVER_CHUNK.COMMIT_BOX.offer(intent);
  }
  //=======================================================================================

  

  //== GLOBAL ==//
  public int getDataId(int x, int y) {
    int cx = toChunkX(x);
    int cy = toChunkY(y);

    Chunk c = WORLD.getChunk(x, y);
    if (c == null) { return CellTypes.OUT_OF_WORLD; }
    return c.getRawDataId(cx, cy);
  }

  public int getDataDeadline(int x, int y) {
    int cx = toChunkX(x);
    int cy = toChunkY(y);
    
    Chunk c = WORLD.getChunk(x, y);
    if (c == null) { return CellTypes.OUT_OF_WORLD; }
    return c.getRawDataDeadline(cx, cy);
  }

  public int getDataLastUpdatedFrame(int x, int y) {
    int cx = toChunkX(x);
    int cy = toChunkY(y);
    
    Chunk c = WORLD.getChunk(x, y);
    if (c == null) { return CellTypes.OUT_OF_WORLD; }
    return c.getRawDataLastUpdatedFrame(cx, cy);
  }
  //=======================================================================================



  //== APPLYING ==//
  private void applyCommit(Intent intent) {
    int receiver_cx = toChunkX(intent.RECEIVER_X);
    int receiver_cy = toChunkY(intent.RECEIVER_Y);
 
    if (intent.ACTIVATION_ONLY) {
      activateCell(receiver_cx, receiver_cy);
      // will activate two times if the same cord.
      if (intent.SENDER_X != intent.RECEIVER_X || intent.SENDER_Y != intent.RECEIVER_Y) {
        intent.SENDER_CHUNK.RESET_BOX.offer(intent);
      }
      return;
    }

    setCell(
      receiver_cx,
      receiver_cy,
      intent.RECEIVER_ID,
      intent.RECEIVER_DEADLINE,
      (intent.RECEIVER_ID != 0) ? WORLD.time() : 0
    );

    intent.SENDER_CHUNK.RESET_BOX.offer(intent);
  }

  private void applyReset(Intent intent) {
    int sender_cx = toChunkX(intent.SENDER_X);
    int sender_cy = toChunkY(intent.SENDER_Y);

    if (intent.ACTIVATION_ONLY) {
      activateCell(sender_cx, sender_cy);
      return;
    }

    setCell(
      sender_cx,
      sender_cy,
      intent.SENDER_ID,
      intent.SENDER_DEADLINE,
      (intent.SENDER_ID != 0) ? WORLD.time() : 0
    );

    IntentPool.free(intent);
  }
  //=======================================================================================



  //== GAME LOGIC ==//
  private void stepCell(int cx, int cy) {
    int x = toGlobalX(cx);
    int y = toGlobalY(cy);

    int cellId = getRawDataId(cx, cy);
    if (cellId == 0) { return; }
                              
    int lastU = getRawDataLastUpdatedFrame(cx, cy);
    if (lastU == WORLD.time()) {
      registerPing(x, y, x, y);
      return; 
    }

    Cell cell = CHolder.get(cellId);
    cell.step(this, x, y);
  }

  // REAL CALLERS
  void process() {
    if (RECT.isEmpty()) { 
      setActive(false); 
      return; 
    }

    setActive(true);

    boolean reverseCX = getRandom().nextBoolean();
    boolean reverseCY = getRandom().nextBoolean();

    for (int dy = RECT.minCY(); dy <= RECT.maxCY(); dy++) {
      int cy = reverseCY ? (RECT.maxCY() - (dy - RECT.minCY())) : dy;
      for (int dx = RECT.minCX(); dx <= RECT.maxCX(); dx++) {
        int cx = reverseCX ? (RECT.maxCX() - (dx - RECT.minCX())) : dx;

        stepCell(cx, cy);
      }
    }

    RECT.clear();
  }

  void commit() {
    if (COMMIT_BOX.isEmpty()) { return; }
    for (Intent current : COMMIT_BOX) {
      if (current.SENDER_CHUNK == null) { continue; }
      if (current.ACTIVATION_ONLY) {
        applyCommit(current);
        continue;
      }

      int cx = toChunkX(current.RECEIVER_X);
      int cy = toChunkY(current.RECEIVER_Y);
      if (getRawDataLastUpdatedFrame(cx, cy) == WORLD.time()) { continue; }
      applyCommit(current);
    }
    COMMIT_BOX.clear();
  }

  // void commit() {
  //   synchronized (COMMIT_BOX) {
  //     if (COMMIT_BOX.isEmpty()) { return; }
  //     for (int i = COMMIT_BOX.size() - 1; i > 0; i--) {
  //       int j = getRandom().nextInt(i + 1);
  //       Intent tmp = COMMIT_BOX.get(i);
  //       COMMIT_BOX.set(i, COMMIT_BOX.get(j));
  //       COMMIT_BOX.set(j, tmp);
  //
  //       Intent current = COMMIT_BOX.get(i);
  //
  //       if (current.ACTIVATION_ONLY) {
  //         applyCommit(current);
  //         continue;
  //       }
  //
  //       int cx = toChunkX(current.RECEIVER_X);
  //       int cy = toChunkY(current.RECEIVER_Y);
  //       if (getRawDataLastUpdatedFrame(cx, cy) == WORLD.time()) { continue; }
  //       applyCommit(current);
  //     }
  //
  //     if (!COMMIT_BOX.isEmpty()) {
  //       Intent first = COMMIT_BOX.get(0);
  //
  //       int cx = toChunkX(first.RECEIVER_X);
  //       int cy = toChunkY(first.RECEIVER_Y);
  //       if (first.ACTIVATION_ONLY) {
  //         applyCommit(first);
  //
  //       } else if (getRawDataLastUpdatedFrame(cx, cy) != WORLD.time()) {
  //         applyCommit(first);
  //       }
  //     }
  //
  //     COMMIT_BOX.clear();
  //   }
  // }

  void reset() {
    if (RESET_BOX.isEmpty()) { return; }
    for (Intent intent : RESET_BOX) {
      applyReset(intent);
    }

    RESET_BOX.clear();
  }
  //=======================================================================================
}
