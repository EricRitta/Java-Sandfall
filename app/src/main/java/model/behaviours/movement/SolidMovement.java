package model.behaviours.movement;
import model.behaviours.interfaces.MovementBehaviour;
import model.cells.Cell;
import model.logic.Chunk;

public class SolidMovement implements MovementBehaviour {
  
  @Override
  public boolean step(Cell c, Chunk chunk, int cx, int cy) {
    boolean sucess = false;

    // under
    if (getCellIn(chunk, cx, cy + 1) == 0) {
      moveTo(c, chunk, cx, cy + 1, cx, cy);
      sucess = true;
      return sucess;
    }

    // verify if sides
    int cellInLeft = getCellIn(chunk, cx - 1, cy + 1);
    int cellInRight = getCellIn(chunk, cx + 1, cy + 1);

    // if both sides open, choose random side
    if (cellInLeft == 0 && cellInRight == 0) {
      boolean choose = chunk.getRandom().nextBoolean();

      // right
      if (choose) {
        moveTo(c, chunk, cx + 1, cy + 1, cx, cy);
        sucess = true;
        return sucess;

      // left
      } else {
        moveTo(c, chunk, cx - 1, cy + 1, cx, cy);
        sucess = true;
        return sucess;
      }

    // right
    } else if (cellInRight == 0) {
      moveTo(c, chunk, cx + 1, cy + 1, cx, cy);
      sucess = true;
      return sucess;

    // left
    } else if (cellInLeft == 0) {
      moveTo(c, chunk, cx - 1, cy + 1, cx, cy);
      sucess = true;
      return sucess;
    }

    return sucess;
  }
}
