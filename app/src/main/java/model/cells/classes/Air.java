package model.cells.classes;

import util.Config;
import model.logic.Chunk;
import model.cells.Cell;

public class Air extends Cell {
  public Air() {
    this.TYPE = Config.get("SOLID");
    this.ID = 0;
  }

  @Override
  public boolean step(Chunk chunk, int cx, int cy) {
    return false;
  }
}
