package model.abstracts;
import model.interfaces.MovementBehaviour;
import model.interfaces.ReactionBehaviour;
import model.logic.Chunk;

public abstract class Cell {
  private MovementBehaviour[] movements;
  private ReactionBehaviour[] reactions;

  public void step(Chunk chunk, int cx, int cy) {}
}
