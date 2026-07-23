package model.interfaces;
import model.logic.World;
import model.abstracts.Cell;

public interface MovementBehaviour {
  public void step(Cell c, World world, int x, int y);
}
