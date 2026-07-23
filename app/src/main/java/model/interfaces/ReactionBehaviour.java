package model.interfaces;
import model.logic.World;
import model.abstracts.Cell;

public interface ReactionBehaviour {
  public void step(Cell c, World world, int x, int y);
}
