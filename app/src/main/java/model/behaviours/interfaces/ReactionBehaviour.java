package model.behaviours.interfaces;
import model.logic.Chunk;
import model.cells.Cell;

public interface ReactionBehaviour {
  default boolean step(Cell c, Chunk chunk, int cx, int cy) {
    boolean success = false;
    // do things that can change sucess state;
    return success;
  }
}
