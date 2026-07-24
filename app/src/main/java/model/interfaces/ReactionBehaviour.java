package model.interfaces;
import model.logic.Chunk;
import model.abstracts.Cell;

public interface ReactionBehaviour {
  public void step(Cell c, Chunk world, int cx, int cy);
}
