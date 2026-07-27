package model.abstracts;
import model.interfaces.MovementBehaviour;
import model.interfaces.ReactionBehaviour;
import model.logic.Chunk;

public abstract class Cell {
  private int ID;
  private MovementBehaviour[] movements;
  private ReactionBehaviour[] reactions;

  public int getID() { return this.ID; }

  public boolean step(Chunk c, int cx, int cy) {
    boolean changed = false;
    return changed;
  }

  public int getSelfDeadline(Chunk c, int cx, int cy) {
    return c.getRawCellDeadline(cx, cy);
  }
}
