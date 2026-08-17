package model.behaviours.interfaces;
import model.behaviours.interfaces.BehaviourHelpers;
import model.logic.Chunk;
import model.cells.Cell;

public interface MovementBehaviour extends BehaviourHelpers {
  public boolean step(Cell c, Chunk chunk, int cx, int cy);

  default void moveTo(Cell c, Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    setCellIn(
      chunk,                                                    // chunk
      toCX,                                                     // X
      toCY,                                                     // Y
      c.getID(),                                                // ID
      chunk.getDataPointIn(fromCX, fromCY, Chunk.CELL_DEADLINE) // Deadline
    );
    resetCellIn(chunk, fromCX, fromCY);
  }

  default void swapWith(Cell c, Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    int toID = getCellIn(chunk, toCX, toCY);
    int toDeadline = getDeadlineIn(chunk, toCX, toCY);
    setCellIn(
      chunk,                                                    // chunk
      toCX,                                                     // X
      toCY,                                                     // Y
      c.getID(),                                                // ID
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
