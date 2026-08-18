package model.cells;
import model.logic.Chunk;

public abstract class Cell {
  protected String TYPE; // use the CellType class to define (CellTypes.POWDER)
  protected int ID; // use the CellType class to define (CellTypes.POWDER_ID + desired id)

  public int getId() { return this.ID; }
  public String getType() { return this.TYPE; }

  public abstract boolean step(Chunk chunk, int cx, int cy);
}
