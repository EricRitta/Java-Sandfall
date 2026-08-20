package model.cells;
import model.logic.Chunk;

public abstract class Cell {
  protected String TYPE; // use the CellType class to define (CellTypes.POWDER)
  protected int ID; // use the CellType class to define (CellTypes.POWDER_ID + desired id)
  protected int DISPERSION_RATE = 1;
  protected int GRAVITY = 1;

  public int getId() { return this.ID; }
  public int getDispersionRate() { return this.DISPERSION_RATE; }
  public int getGravity() { return this.GRAVITY; }
  public String getType() { return this.TYPE; }

  public abstract boolean step(Chunk chunk, int cx, int cy);
}
