package model.behaviours.interfaces;
import model.logic.Chunk;
import model.cells.Cell;

public interface MovementBehaviour {
  default boolean step(Cell c, Chunk chunk, int cx, int cy) {
    boolean success = false;
    // do things that can change sucess state;
    return success;
  }

  default void moveTo(Cell c, Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    chunk.setCellIn(toCX, toCY, c.getID(), c.getSelfDeadline(chunk, fromCX, fromCY));
    chunk.setCellIn(fromCX, fromCY, 0, 0, 0); // reset to air
  }

  default void swapWith(Cell c, Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    int toID = getCellIn(chunk, toCX, toCY);
    int toDeadline = getDeadlineIn(chunk, toCX, toCY);
    chunk.setCellIn(toCX, toCY, c.getID(), c.getSelfDeadline(chunk, fromCX, fromCY));
    chunk.setCellIn(fromCX, fromCY, toID, toDeadline, 0);
  }

  // helpers
  default int getCellIn(Chunk chunk, int cx, int cy) {
    return chunk.getCellIn(cx, cy);
  }
  default int getDeadlineIn(Chunk chunk, int cx, int cy) {
    return chunk.getDeadlineIn(cx, cy);
  }
}
