package model.behaviours.movement;
import model.interfaces.MovementBehaviour;
import model.abstracts.Cell;
import model.logic.Chunk;

public class SolidMovement implements MovementBehaviour {
  
  @Override
  public boolean step(Cell c, Chunk chunk, int cx, int cy) {
    boolean sucess = false;
    // TODO: SolidMovement logic
    return sucess;
  }
}
