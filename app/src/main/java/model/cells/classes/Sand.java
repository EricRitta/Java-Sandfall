package model.cells.classes;

import model.logic.Chunk;
import util.Config;
import model.cells.Cell;

public class Sand extends Cell {
  public Sand() {
    this.TYPE = Config.get("POWDER");
  }
}
