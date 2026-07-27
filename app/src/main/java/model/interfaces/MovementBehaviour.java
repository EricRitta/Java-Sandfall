package model.interfaces;
import model.logic.Chunk;
import model.abstracts.Cell;

public interface MovementBehaviour {
  default boolean step(Cell c, Chunk chunk, int cx, int cy) {
    boolean success = false;
    // do things that can change sucess state;
    return success;
  }

  private void moveTo(Cell c, Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    chunk.setCellIn(toCX, toCY, c.getID(), c.getSelfDeadline(chunk, fromCX, fromCY));
    chunk.clearCell(fromCX, fromCY);
  }

  private void swapTo(Cell c, Chunk chunk, int toCX, int toCY, int fromCX, int fromCY) {
    int toID = chunk.getCellIn(toCX, toCY);
    int toDeadline = chunk.getCellDeadlineIn(toCX, toCY);
    chunk.setCellIn(toCX, toCY, c.getID(), c.getSelfDeadline(chunk, fromCX, fromCY));
    chunk.setCellIn(fromCX, fromCY, toID, toDeadline);
    chunk.setCellWasMovedIn(fromCX, fromCY, 0);
  }
}
