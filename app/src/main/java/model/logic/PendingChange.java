package model.logic;
import model.logic.Chunk;

public class PendingChange {
  public final Chunk TARGET;
  public final int ID;
  public final int DEADLINE;
  public final int LAST_MOVED;
  public final int CX;
  public final int CY;

  public PendingChange(Chunk target, int cx, int cy, int id, int dl, int lm) {
    this.TARGET = target;
    this.CX = cx;
    this.CY = cy;
    this.ID = id;
    this.DEADLINE = dl;
    this.LAST_MOVED = lm;
  }
}
