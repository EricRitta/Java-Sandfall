package model.behaviours.movement;
import model.behaviours.interfaces.MovementBehaviour;
import model.cells.Cell;
import model.logic.Chunk;

public class SolidMovement implements MovementBehaviour {
  
  @Override
  public boolean step(Cell c, Chunk chunk, int cx, int cy) {
    boolean sucess = false;

    //under
    if (getCellIn(chunk, cx, cy + 1) == 0) {
      moveTo(c, chunk, cx, cy + 1, cx, cy);
      sucess = true;

    // left
    } else if (getCellIn(chunk, cx - 1, cy + 1) == 0) {
      moveTo(c, chunk, cx - 1, cy + 1, cx, cy);
      sucess = true;

    // right
    } else if (getCellIn(chunk, cx + 1, cy + 1) == 0) {
      moveTo(c, chunk, cx + 1, cy + 1, cx, cy);
      sucess = true;
    }

    return sucess;
  }
}
