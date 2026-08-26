package model.components.movement;

import model.components.basics.Movement;
import model.universe.Chunk;

public interface PowderMovement extends Movement {
  default boolean tryPowderMovement(Chunk chunk, int x, int y) {
    boolean success = false;

    // under
    success = fallTo(chunk, x, y);
    if (success) { return success; }

    boolean checkLeftFirst = chunk.getRandom().nextBoolean();

    if (checkLeftFirst) {
      if (getCellIn(chunk, x - 1, y + 1) == 0) {
        moveTo(chunk, x, y, x - 1, y + 1);
        return true;
      }
      if (getCellIn(chunk, x + 1, y + 1) == 0) {
        moveTo(chunk, x, y, x + 1, y + 1);
        return true;
      }
    } else {
      if (getCellIn(chunk, x + 1, y + 1) == 0) {
        moveTo(chunk, x, y, x + 1, y + 1);
        return true;
      }
      if (getCellIn(chunk, x - 1, y + 1) == 0) {
        moveTo(chunk, x, y, x - 1, y + 1);
        return true;
      }
    }

    return false;
  }
}
