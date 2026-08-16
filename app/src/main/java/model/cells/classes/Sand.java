package model.cells.classes;

import model.behaviours.interfaces.MovementBehaviour;
import model.behaviours.movement.SolidMovement;

import util.Config;
import model.cells.Cell;

public class Sand extends Cell {
  public Sand() {
    this.TYPE = Config.get("POWDER");
    this.ID = Config.getId("POWDER") + 1;
    this.MOVEMENTS = new MovementBehaviour[]{ 
      new SolidMovement() 
    };
  }
}
