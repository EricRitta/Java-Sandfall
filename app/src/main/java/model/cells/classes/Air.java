package model.cells.classes;

import model.logic.Chunk;
import model.cells.CTypes;
import model.abstracts.Cell;

public class Air extends Cell {
  public Air() {
    this.TYPE = CTypes.SOLID;
    this.ID = 0;
  }

  @Override
  public boolean step(Chunk chunk, int cx, int cy) {
    return false;
  }
}
