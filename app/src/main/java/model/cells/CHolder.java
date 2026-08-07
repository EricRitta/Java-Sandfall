package model.cells;

import model.abstracts.Cell;
import model.cells.classes.*;

public class CHolder {
  private static final Cell[] catalogue = new Cell[] {
    new Sand(),
  };

  private static final int SIZE = 4096;
  private static final Cell[] ALL_CELLS = new Cell[SIZE];

  static {
    for (Cell c : catalogue) {
      int cID = c.getID();
      Cell desiredCellPos = ALL_CELLS[cID];
      if (desiredCellPos != null) { 
        throw new IllegalStateException("Desired cell position already taken. ID: " + cID); 
      }
      ALL_CELLS[cID] = c;
    }
  }

  public static Cell get(int id) {
    Cell c = ALL_CELLS[id];
    if (c == null) {
      throw new IllegalStateException("Desired cell doesn't exist. ID: " + id);
    }
    return c;
  }
}
