package model.cells;

import model.abstracts.Cell;
import model.cells.classes.*;

public class CHolder {
  private static final Cell[] ALL_CELLS = new Cell[] {
    new Sand(),
  };

  @SuppressWarnings("unchecked")
  private static final Class<? extends Cell>[] ALL_CELLS = new Class[] {
    // Sand.class
    // water.class
    // etc.
  };

  private static final Map<Integer, Cell> cast = new HashMap<Integer, Cell>();

  public static void registerCells() {
    for (Class<? extends Cell> clazz : ALL_CELLS) {
      try {
        Cell instance = clazz.getDeclaredConstructor().newInstance();
        cast.put(instance.getID(), instance);
      } catch (Exception e) {
        throw new RuntimeException("Error when trying to instanciate " + clazz.getSimpleName(), e);
      }
    }
  }

  public static Cell get(int id) {
    Cell cell = cast.get(id);
    if (cell == null) { 
      throw new IllegalArgumentException("No cell with id: " + id + " found.");
    }
    return cell;
  }
}
