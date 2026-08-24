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
    chunk.registerActivation(fromX, fromY, toX, toY);
  }

  //== MOVEMENT BASICS ==//
  default void moveTo(Chunk chunk, int fromCX, int fromCY, int toCX, int toCY) {
    chunk.registerIntent(
      false,
      "to",
      chunk.getGlobalX(fromCX),
      chunk.getGlobalY(fromCY),
      chunk.getGlobalX(toCX),
      chunk.getGlobalY(toCY),
      0, 
      0,
      getId(),
      getLastMovedIn(chunk, fromCX, fromCY)
    );
  }
  default void swapWith(Chunk chunk, int fromCX, int fromCY, int toCX, int toCY) {
    chunk.registerIntent(
      false,
      "to",
      chunk.getGlobalX(fromCX),
      chunk.getGlobalY(fromCY),
      chunk.getGlobalX(toCX),
      chunk.getGlobalY(toCY),
      getCellIn(chunk, toCX, toCY), 
      getDeadlineIn(chunk, toCX, toCY),
      getId(),
      getLastMovedIn(chunk, fromCX, fromCY)
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
    moveTo(chunk, toX, toY, fromX, fromY);
    return true;
  }

  // TODO: add acceleration
  default boolean fallTo(Chunk chunk, int fromCX, int fromCY) {
    int toCY = fromCY;

    for (int i = 1; i <= getGravity(); i++) {
      int checkCY = fromCY + i;

      if (getCellIn(chunk, fromCX, checkCY) == 0) {
        toCY = checkCY;
        activatePos(chunk, fromCX, fromCY, fromCX, toCY);
      } else {
        break;
      }
    }

    if (toCY == fromCY) { return false; }
    moveTo(chunk, fromCX, toCY, fromCX, fromCY);
    return true;
  }
}
