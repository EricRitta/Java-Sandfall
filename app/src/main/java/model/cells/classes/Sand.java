package model.cells.classes;

import model.behaviours.movement.SolidMovement;

import model.logic.Chunk;
import util.Config;
import model.cells.Cell;

public class Sand extends Cell {
  public Sand() {
    this.TYPE = Config.get("POWDER");
    this.ID = Config.getId("POWDER") + 1;
    this.MOVEMENTS = {new SolidMovement()};

  }

  @Override
  public boolean step(Chunk chunk, int cx, int cy) {
    return true;
  }
}
