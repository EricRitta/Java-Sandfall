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
  final List<Intent> COMMIT_INBOX = new ArrayList<>();
  final List<Intent> RESET_INBOX = new ArrayList<>();

  final List<Intent> COMMIT_OUTBOX = new ArrayList<>();
  final List<Intent> RESET_OUTBOX = new ArrayList<>();

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



  //== DATA ==//
  private int dataIndex(int cx, int cy) {
    return (cy * CHUNK_SIZE + cx) * FIELDS;
  }
  private boolean inBounds(int cx, int cy) {
    return cx >= 0 && cx < CHUNK_SIZE && cy >= 0 && cy < CHUNK_SIZE;
  }

  // 0 = nenhuma
  // 1 = esquerda
  // 2 = direita
  // 3 = topo
  // 4 = baixo
  // 5 = canto superior-esquerdo
  // 6 = canto superior-direito
  // 7 = canto inferior-esquerdo
  // 8 = canto inferior-direito

  private int getBorder(int cx, int cy) {
      final int last = CHUNK_SIZE - 1;

      if (cx == 0) {
          if (cy == 0) return 5;
          if (cy == last) return 7;
          return 1;
      }

      if (cx == last) {
          if (cy == 0) return 6;
          if (cy == last) return 8;
          return 2;
      }

      if (cy == 0) return 3;
      if (cy == last) return 4;

      return 0;
  }
  void setRawData(int cx, int cy, int pos, int value) {
    DATA[dataIndex(cx, cy) + pos] = value;
  }
  int getRawData(int cx, int cy, int pos) {
    return DATA[dataIndex(cx, cy) + pos];
  }
  //=======================================================================================
  


  //== COORDS ==//
  public int toGlobalX(int cx) {
    return WORLD.toGlobalX(getIndex(), cx);
  }
  public int toGlobalY(int cy) {
    return WORLD.toGlobalY(getIndex(), cy);
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
  public void registerActivation(int senderX, int senderY, int receiverX, int receiverY) {
    registerIntent(true, senderX, senderY, receiverX, receiverY, 0, 0, 0, 0);
  }

  public void registerIntent(
      boolean activeOnly,
      int senderX, int senderY, 
      int receiverX, int receiverY, 
      int senderId, int senderDl, int receiverId, int receiverDl
      )
  {
    if (!inBounds(toChunkX(senderX), toChunkY(senderY))) {
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

    if (intent.RECEIVER_CHUNK == this) {
      COMMIT_INBOX.add(intent);
    } else {
      COMMIT_OUTBOX.add(intent);
    }
  }

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
    if (c == null) { throw new IllegalArgumentException("Chunk out of bounds in: {" + x + ", " + y + "}"); }
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

    // kinda sus code, if problem in the future, ik where to look
    if (!inBounds(cx, cy)) {
      Chunk neighbor = WORLD.getChunk(x, y);
      if (neighbor != null) {
        int myCx = cx < 0 ? 0 : (cx >= CHUNK_SIZE ? CHUNK_SIZE - 1 : cx);
        int myCy = cy < 0 ? 0 : (cy >= CHUNK_SIZE ? CHUNK_SIZE - 1 : cy);
        
        int myId = getRawData(myCx, myCy, CELL_ID);
        int neighborId = neighbor.getRawData(toChunkX(x), toChunkY(y), CELL_ID);

        // wake up neighbro only if we are air
        if (myId == 0 && neighborId != 0) {
          registerActivation(x, y, x, y);
        }
      }
      return;
    }

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

    for (int i = RESET_INBOX.size() - 1; i > 0; i--) {
      int j = getRandom().nextInt(i + 1);
      Intent tmp = RESET_INBOX.get(i);
      RESET_INBOX.set(i, RESET_INBOX.get(j));
      RESET_INBOX.set(j, tmp);

      Intent current = RESET_INBOX.get(i);

      applyResetIntent(current);
    }

    if (!RESET_INBOX.isEmpty()) {
      Intent first = RESET_INBOX.get(0);
      applyResetIntent(first);
    }

  }
  //=======================================================================================
}
