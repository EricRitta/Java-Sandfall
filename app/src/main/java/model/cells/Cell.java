package model.cells;
import model.behaviours.interfaces.MovementBehaviour;
import model.behaviours.interfaces.ReactionBehaviour;
import model.logic.Chunk;

public abstract class Cell {
  protected String TYPE; // use the Config class to define (Config.get("POWDER"))
  protected int ID; // use the Config class to define (Config.getId("POWDER") + desired id)
  protected MovementBehaviour[] MOVEMENTS = new MovementBehaviour[0];
  protected ReactionBehaviour[] REACTIONS = new ReactionBehaviour[0];

  public int getID() { return this.ID; }
  public String getType() { return this.TYPE; }

  public boolean step(Chunk chunk, int cx, int cy) {
    boolean updated = false;

    for (ReactionBehaviour r : REACTIONS) {
      boolean success = r.step(this, chunk, cx, cy);
      if (success) { updated = true; }
    }

    for (MovementBehaviour m : MOVEMENTS) {
      boolean success = m.step(this, chunk, cx, cy);
      if (success) { updated = true; }
    }

    return updated;
  }
}
