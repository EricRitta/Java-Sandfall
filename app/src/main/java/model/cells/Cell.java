package model.cells;
import model.universe.Chunk;

public abstract class Cell {
  protected String TYPE; // use the CellType class to define (CellTypes.POWDER)
  protected int ID; // use the CellType class to define (CellTypes.POWDER_ID + desired id)
  protected int DISPERSION_RATE = 1;
  protected int GRAVITY = 1;

  public int id() { return this.ID; }
  public int dispersionRate() { return this.DISPERSION_RATE; }
  public int gravity() { return this.GRAVITY; }
  public String type() { return this.TYPE; }

  public abstract boolean step(Chunk chunk, int x, int y);
}
