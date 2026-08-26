package model.components.basics;

import model.components.basics.CellGetters;
import model.universe.Chunk;

public interface Movement extends CellGetters {
  int dispersionRate();
  int gravity();
  int id();
  String type();
  
  //== PRIVATE ==//
  private void pingNeighbors(Chunk chunk, int x, int y) {
    int localX = chunk.toChunkX(x);
    int localY = chunk.toChunkY(y);
    int chunkSize = chunk.size();

    int pingX = 0, pingY = 0;

    if (localX == 0) { pingX = -1; }
    if (localX == chunkSize - 1) { pingX = 1; }
    if (localY == 0) { pingY = -1; }
    if (localY == chunkSize - 1) { pingY = 1; }

    if (pingX != 0) { pingPosition(chunk, x, y, x + pingX, y); }
    if (pingY != 0) { pingPosition(chunk, x, y, x, y + pingY); }
    if (pingX != 0 && pingY != 0) { pingPosition(chunk, x, y, x + pingX, y + pingY); }
  }
  //=======================================================================================



  //== MOVEMENT BASICS ==//
  default void pingPosition(Chunk chunk, int fromX, int fromY, int toX, int toY) {
    chunk.registerPing(
      fromX,
      fromY,
      toX,
      toY
    );
  }

  default void moveTo(Chunk chunk, int fromX, int fromY, int toX, int toY) {
    pingNeighbors(chunk, fromX, fromY);
    // pingNeighbors(chunk, toX, toY);

    chunk.registerIntent(
      false,
      fromX,
      fromY,
      toX,
      toY,
      0, 
      0,
      id(),
      getLastMovedIn(chunk, fromX, fromY)
    );
  }
  default void swapWith(Chunk chunk, int fromX, int fromY, int toX, int toY) {
    pingNeighbors(chunk, fromX, fromY);
    // pingNeighbors(chunk, toX, toY);

    chunk.registerIntent(
      false,
      fromX,
      fromY,
      toX,
      toY,
      getCellIn(chunk, toX, toY), 
      getDeadlineIn(chunk, toX, toY),
      id(),
      getLastMovedIn(chunk, fromX, fromY)
    );
  }
  //=======================================================================================



  //== MOVEMENT ADVANCED ==//
  default boolean disperseTo(Chunk chunk, int fromX, int fromY, int direction) {
    int toX = fromX;
    int toY = fromY;

    for (int i = 1; i <= dispersionRate(); i++) {
      int checkX = fromX + (direction * i);

      if (getCellIn(chunk, checkX, fromY + 1) == 0) {
        toX = checkX;
        toY = fromY + 1;
        pingPosition(chunk, fromX, fromY, toX, toY);
        break;
      }

      if (getCellIn(chunk, checkX, fromY) == 0) {
        toX = checkX;
        toY = fromY;
        pingPosition(chunk, fromX, fromY, toX, toY);
      } else {
        break;
      }
    }

    if (toX == fromX && toY == fromY) { return false; }
    moveTo(chunk, fromX, fromY, toX, toY);
    return true;
  }

  // TODO: add acceleration
  default boolean fallTo(Chunk chunk, int fromX, int fromY) {
    int toY = fromY;

    for (int i = 1; i <= gravity(); i++) {
      int checkY = fromY + i;

      if (getCellIn(chunk, fromX, checkY) == 0) {
        toY = checkY;
        pingPosition(chunk, fromX, fromY, fromX, toY);
      } else {
        break;
      }
    }

    if (toY == fromY) { return false; }
    moveTo(chunk, fromX, fromY, fromX, toY);
    return true;
  }
  //=======================================================================================
}
