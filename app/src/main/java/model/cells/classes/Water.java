package model.cells.classes;

import util.CellTypes;
import model.cells.Cell;
import model.logic.Chunk;
import model.components.movement.*;

public class Water extends Cell implements LiquidMovement {
  public Water() {
    this.TYPE = CellTypes.LIQUID;
    this.ID = CellTypes.LIQUID_ID + 1;
    this.DISPERSION_RATE = 5;
  }

  public boolean step(Chunk chunk, int cx, int cy) {
    return tryLiquidMovement(chunk, cx, cy);
  }
}
