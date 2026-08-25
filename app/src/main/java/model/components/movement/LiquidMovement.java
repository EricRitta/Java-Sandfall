package model.components.movement;

import model.components.basics.Movement;
import model.logic.Chunk;

public interface LiquidMovement extends Movement {
  default boolean tryLiquidMovement(Chunk chunk, int x, int y) {
    boolean success = false;

    // under
    success = fallTo(chunk, x, y);
    if (success) { return success; }

    // verify if sides
    boolean choose = chunk.getRandom().nextBoolean();
    if (choose) {
      success = disperseTo(chunk, x, y, 1);
      if (!success) {
        success = disperseTo(chunk, x, y, -1);
      }
    } else {
      success = disperseTo(chunk, x, y, -1);
      if (!success) {
        success = disperseTo(chunk, x, y, 1);
      }
    }

    return success;
  }
}
