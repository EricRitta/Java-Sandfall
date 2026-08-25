package model.components.basics;
import model.components.basics.*;
import model.logic.Chunk;

public interface Movement extends CellGetters {
  int getDispersionRate();
  int getGravity();
  int getId();
  String getType();
  
  //== SETS ==//
  default void activatePos(Chunk chunk, int fromX, int fromY, int toX, int toY) {
    chunk.registerActivation(
      fromX,
      fromY,
      toX,
      toY
    );
  }

  //== MOVEMENT BASICS ==//
  default void moveTo(Chunk chunk, int fromX, int fromY, int toX, int toY) {
    chunk.registerIntent(
      false,
      "to",
      fromX,
      fromY,
      toX,
      toY,
      0, 
      0,
      getId(),
      getLastMovedIn(chunk, fromX, fromY)
    );
  }
  default void swapWith(Chunk chunk, int fromX, int fromY, int toX, int toY) {
    chunk.registerIntent(
      false,
      "to",
      fromX,
      fromY,
      toX,
      toY,
      getCellIn(chunk, toX, toY), 
      getDeadlineIn(chunk, toX, toY),
      getId(),
      getLastMovedIn(chunk, fromX, fromY)
    );
  }

  //== MOVEMENT ADVANCED ==//
  default boolean disperseTo(Chunk chunk, int fromX, int fromY, int direction) {
    int toX = fromX;
    int toY = fromY;

    for (int i = 1; i <= getDispersionRate(); i++) {
      int checkX = fromX + (direction * i);

      if (getCellIn(chunk, checkX, fromY + 1) == 0) {
        toX = checkX;
        toY = fromY + 1;
        activatePos(chunk, fromX, fromY, toX, toY);
        break;
      }

      if (getCellIn(chunk, checkX, fromY) == 0) {
        toX = checkX;
        toY = fromY;
        activatePos(chunk, fromX, fromY, toX, toY);
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

    for (int i = 1; i <= getGravity(); i++) {
      int checkY = fromY + i;

      if (getCellIn(chunk, fromX, checkY) == 0) {
        toY = checkY;
        activatePos(chunk, fromX, fromY, fromX, toY);
      } else {
        break;
      }
    }

    if (toY == fromY) { return false; }
    moveTo(chunk, fromX, fromY, fromX, toY);
    return true;
  }
}
