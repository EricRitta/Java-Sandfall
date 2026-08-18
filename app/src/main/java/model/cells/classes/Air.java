package model.cells.classes;

import util.CellTypes;
import model.logic.Chunk;
import model.cells.Cell;

public class Air extends Cell {
  public Air() {
    this.TYPE = CellTypes.POWDER;
    this.ID = 0;
  }

  public boolean step(Chunk chunk, int cx, int cy) {
    return false;
  }
}
