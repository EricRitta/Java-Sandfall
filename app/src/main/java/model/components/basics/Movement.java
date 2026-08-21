package model.components.basics;
import model.components.basics.*;
import model.logic.Chunk;

public interface Movement extends CellGetters {
  int getDispersionRate();
  int getGravity();
  int getId();
  String getType();
    
  //== SETS ==//
  private void setCellIn(Chunk chunk, int cx, int cy, int id, int dl, boolean lmbool) {
    chunk.requestChange(cx, cy, id, dl, lmbool);
  }
  private void resetCellIn(Chunk chunk, int cx, int cy) {
    chunk.requestChange(cx, cy, 0, 0, false);
  }
  
  //== MOVEMENT BASICS ==//
  default void moveTo(Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    setCellIn(
      chunk,                                                     // chunk
      toCX,                                                      // X
      toCY,                                                      // Y
      getId(),                                                   // ID
      chunk.getDataPointIn(fromCX, fromCY, Chunk.CELL_DEADLINE), // Deadline
      true                                                       // Last Moved
    );
    resetCellIn(chunk, fromCX, fromCY);
  }
  default void swapWith(Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    int toID = getCellIn(chunk, toCX, toCY);
    int toDeadline = getDeadlineIn(chunk, toCX, toCY);
    setCellIn(
      chunk,                                                     // chunk
      toCX,                                                      // X
      toCY,                                                      // Y
      getId(),                                                   // ID
      chunk.getDataPointIn(fromCX, fromCY, Chunk.CELL_DEADLINE), // Deadline
      true                                                       // Last Moved
    );
    setCellIn(
      chunk,            // chunks
      fromCX,           // X
      fromCY,           // Y
      toID,             // ID
      toDeadline,       // deadline
      false             // lastMoved
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
        chunk.activateCell(toCX, toCY);
        break;
      }

      if (getCellIn(chunk, checkCX, fromCY) == 0) {
        toCX = checkCX;
        toCY = fromCY;
        chunk.activateCell(toCX, toCY);
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
        chunk.activateCell(fromCX, toCY);
      } else {
        break;
      }
    }

    if (toCY == fromCY) { return false; }
    moveTo(chunk, fromCX, toCY, fromCX, fromCY);
    return true;
  }
}
