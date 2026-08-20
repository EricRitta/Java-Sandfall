package model.components.movement;

import model.components.basics.Movement;
import model.logic.Chunk;

public interface LiquidMovement extends Movement {
  private boolean disperse(Chunk chunk, int fromCX, int fromCY, int dx, int dy) {
    int toCX = fromCX;
    int toCY = fromCY;

    for (int i = 1; i <= 5; i++) { // TODO: replace 5 with friction or something like that
      int checkCX = fromCX + (dx * i);
      int checkCY = fromCY + (dy * i);

      if (getCellIn(chunk, checkCX, checkCY) == 0) {
        toCX = checkCX;
        toCY = checkCY;
        chunk.activateCell(toCX, toCY);
      } else {
        break;
      }
    }

    if (toCX == fromCX && toCY == fromCY) { return false; }
    moveTo(chunk, toCX, toCY, fromCX, fromCY);
    return true;
  }

  default boolean tryLiquidMovement(Chunk chunk, int cx, int cy) {
    boolean sucess = false;

    // under
    if (getCellIn(chunk, cx, cy + 1) == 0) {
      moveTo(chunk, cx, cy + 1, cx, cy);
      sucess = true;
      return sucess;
    }

    // verify if sides
    int cellInLeft = getCellIn(chunk, cx - 1, cy);
    int cellInRight = getCellIn(chunk, cx + 1, cy);

    // if both sides open, choose random side
    if (cellInLeft == 0 && cellInRight == 0) {
      boolean choose = chunk.getRandom().nextBoolean();

      // random right
      if (choose) {
        // sucess = disperse(chunk, cx + 1, cy, 1, 0);
        // return sucess;
        moveTo(chunk, cx + 1, cy, cx, cy);
        sucess = true;
        return sucess;

      // random left
      } else {
        // sucess = disperse(chunk, cx - 1, cy, -1, 0);
        // return sucess;
        moveTo(chunk, cx - 1, cy, cx, cy);
        sucess = true;
        return sucess;
      }

    // right
    } else if (cellInRight == 0) {
      // sucess = disperse(chunk, cx + 1, cy, 1, 0);
      // return sucess;
      moveTo(chunk, cx + 1, cy, cx, cy);
      sucess = true;
      return sucess;

    // left
    } else if (cellInLeft == 0) {
      // sucess = disperse(chunk, cx + 1, cy, 1, 0);
      // return sucess;
      moveTo(chunk, cx - 1, cy, cx, cy);
      sucess = true;
      return sucess;
    }

    return sucess;
  }
}
