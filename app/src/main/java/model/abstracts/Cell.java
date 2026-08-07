package model.abstracts;
import model.interfaces.MovementBehaviour;
import model.interfaces.ReactionBehaviour;
import model.logic.Chunk;

public abstract class Cell {
  protected String TYPE; // use the CellType class to define (CellType.LIQUID)
  protected int ID;
  protected MovementBehaviour[] MOVEMENTS;
  protected ReactionBehaviour[] REACTIONS;

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

  public void activateSelf(Chunk chunk, int cx, int cy) {
    chunk.activateCell(cx, cy);
  }

  public int getSelfDeadline(Chunk chunk, int cx, int cy) {
    return chunk.getRawDataPoint(cx, cy, 1);
  }
}
