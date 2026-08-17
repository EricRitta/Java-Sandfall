package model.cells.classes;

import model.behaviours.interfaces.MovementBehaviour;
import model.behaviours.movement.SolidMovement;

import util.CellTypes;
import model.cells.Cell;

public class Sand extends Cell {
  public Sand() {
    this.TYPE = CellTypes.POWDER;
    this.ID = CellTypes.POWDER_ID + 1;
    this.MOVEMENTS = new MovementBehaviour[]{ 
      new SolidMovement() 
    };
  }
}
