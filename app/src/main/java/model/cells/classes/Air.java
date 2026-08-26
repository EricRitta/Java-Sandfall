package model.cells.classes;

import settings.CellTypes;
import model.universe.Chunk;
import model.cells.Cell;

public class Air extends Cell {
  public Air() {
    this.TYPE = CellTypes.POWDER;
    this.ID = 0;
  }

  public boolean step(Chunk chunk, int x, int y) {
    return false;
  }
}
