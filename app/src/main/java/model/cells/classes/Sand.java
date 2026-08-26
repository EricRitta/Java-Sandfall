package model.cells.classes;

import settings.CellTypes;
import model.cells.Cell;
import model.universe.Chunk;
import model.components.movement.*;

public class Sand extends Cell implements PowderMovement {
  public Sand() {
    this.TYPE = CellTypes.POWDER;
    this.ID = CellTypes.POWDER_ID + 1;
  }

  public boolean step(Chunk chunk, int x, int y) {
    return tryPowderMovement(chunk, x, y);
  }
}
