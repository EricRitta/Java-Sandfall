package model.components.movement;

import model.components.basics.Movement;
import model.logic.Chunk;

public interface PowderMovement extends Movement {
  default boolean tryPowderMovement(Chunk chunk, int x, int y) {
    boolean success = false;

    // under
    success = fallTo(chunk, x, y);
    if (success) { return success; }

    if (getCellIn(chunk, x - 1, y + 1) == 0) {
      moveTo(chunk, x, y, x - 1, y + 1);
      success = true;
      return success;
    }

    if (getCellIn(chunk, x + 1, y + 1) == 0) {
      moveTo(chunk, x, y, x + 1, y + 1);
      success = true;
      return success;
    }

    return success;
  }
}
