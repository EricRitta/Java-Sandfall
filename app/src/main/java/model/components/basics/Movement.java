package model.components.basics;
import model.logic.Chunk;

public interface Movement {
  int getId();
  String getType();
    
  //== SETS ==//
  private void setCellIn(Chunk chunk, int cx, int cy, int id, int deadline) {
    chunk.setDataPointIn(cx, cy, id, deadline);
  }
  private void setCellIn(Chunk chunk, int cx, int cy, int id, int deadline, int lastMoved) {
    chunk.setDataPointIn(cx, cy, id, deadline, lastMoved);
  }
  private void resetCellIn(Chunk chunk, int cx, int cy) {
    chunk.resetDataPointIn(cx, cy);
  }
  
  //== GETS ==//
  default int getCellIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Chunk.CELL_ID);
  }
  default int getDeadlineIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Chunk.CELL_DEADLINE);
  }
  default int getLastMovedIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Chunk.CELL_LAST_MOVED);
  }

  //== MOVEMENT BASICS ==//
  default void moveTo(Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    setCellIn(
      chunk,                                                    // chunk
      toCX,                                                     // X
      toCY,                                                     // Y
      getId(),                                                  // ID
      chunk.getDataPointIn(fromCX, fromCY, Chunk.CELL_DEADLINE) // Deadline
    );
    resetCellIn(chunk, fromCX, fromCY);
  }
  default void swapWith(Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    int toID = getCellIn(chunk, toCX, toCY);
    int toDeadline = getDeadlineIn(chunk, toCX, toCY);
    setCellIn(
      chunk,                                                    // chunk
      toCX,                                                     // X
      toCY,                                                     // Y
      getId(),                                                  // ID
      chunk.getDataPointIn(fromCX, fromCY, Chunk.CELL_DEADLINE) // Deadline
    );
    setCellIn(
      chunk,        // chunks
      fromCX,       // X
      fromCY,       // Y
      toID,         // ID
      toDeadline,   // deadline
      0             // lastMoved
    );
  }
}
