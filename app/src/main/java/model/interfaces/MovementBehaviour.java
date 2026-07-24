package model.interfaces;
import model.logic.Chunk;
import model.abstracts.Cell;

public interface MovementBehaviour {
  public void step(Cell c, Chunk chunk, int cx, int cy);
}
