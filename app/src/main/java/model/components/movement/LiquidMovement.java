package model.components.movement;

import model.components.basics.Movement;
import model.logic.Chunk;

public interface LiquidMovement extends Movement {
  default boolean tryLiquidMovement(Chunk chunk, int cx, int cy) {
    boolean success = false;

    // under
    success = fallTo(chunk, cx, cy);
    if (success) { return success; }

    // verify if sides
    boolean choose = chunk.getRandom().nextBoolean();
    if (choose) {
      success = disperseTo(chunk, cx, cy, 1);
      if (!success) {
        success = disperseTo(chunk, cx, cy, -1);
      }
    } else {
      success = disperseTo(chunk, cx, cy, -1);
      if (!success) {
        success = disperseTo(chunk, cx, cy, 1);
      }
    }

    return success;
  }
}
