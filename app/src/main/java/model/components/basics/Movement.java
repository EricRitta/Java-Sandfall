package model.components.basics;
import model.components.basics.*;
import model.logic.Chunk;

public interface Movement extends CellGetters {
  int getDispersionRate();
  int getGravity();
  int getId();
  String getType();
    
  //== MOVEMENT BASICS ==//
  default void moveTo(Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    chunk.registerIntent(
      fromCX,
      fromCY,
      toCX,
      toCY,
      0, 
      0,
      getId(),
      getLastMovedIn(chunk, fromCX, fromCY)
    );
  }
  default void swapWith(Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    chunk.registerIntent(
      fromCX,
      fromCY,
      toCX,
      toCY,
      getCellIn(chunk, toCX, toCY), 
      getDeadlineIn(chunk, toCX, toCY),
      getId(),
      getLastMovedIn(chunk, fromCX, fromCY)
    );
  }

  //== MOVEMENT ADVANCED ==//
  default boolean disperseTo(Chunk chunk, int fromCX, int fromCY, int direction) {
    int toCX = fromCX;
    int toCY = fromCY;

    for (int i = 1; i <= getDispersionRate(); i++) {
      int checkCX = fromCX + (direction * i);

      if (getCellIn(chunk, checkCX, fromCY + 1) == 0) {
        toCX = checkCX;
        toCY = fromCY + 1;
        chunk.registerActivation(toCX, toCY);
        break;
      }

      if (getCellIn(chunk, checkCX, fromCY) == 0) {
        toCX = checkCX;
        toCY = fromCY;
        chunk.registerActivation(toCX, toCY);
      } else {
        break;
      }
    }

    if (toCX == fromCX && toCY == fromCY) { return false; }
    moveTo(chunk, toCX, toCY, fromCX, fromCY);
    return true;
  }

  // TODO: add acceleration
  default boolean fallTo(Chunk chunk, int fromCX, int fromCY) {
    int toCY = fromCY;

    for (int i = 1; i <= getGravity(); i++) {
      int checkCY = fromCY + i;

      if (getCellIn(chunk, fromCX, checkCY) == 0) {
        toCY = checkCY;
        chunk.registerActivation(fromCX, toCY);
      } else {
        break;
      }
    }

    if (toCY == fromCY) { return false; }
    moveTo(chunk, fromCX, toCY, fromCX, fromCY);
    return true;
  }
}
