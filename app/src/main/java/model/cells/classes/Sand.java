package model.cells.classes;

import util.CellTypes;
import model.cells.Cell;
import model.logic.Chunk;
import model.components.movement.*;

public class Sand extends Cell implements PowderMovement {
  public Sand() {
    this.TYPE = CellTypes.POWDER;
    this.ID = CellTypes.POWDER_ID + 1;
  }

  public boolean step(Chunk chunk, int cx, int cy) {
    return tryPowderMovement(chunk, cx, cy);
  }
}
