package model.behaviours.interfaces;
import model.behaviours.interfaces.BehaviourHelpers;
import model.logic.Chunk;
import model.cells.Cell;

public interface ReactionBehaviour extends BehaviourHelpers {
  public boolean step(Cell c, Chunk chunk, int cx, int cy);
}
