package model.components.basics;

import model.logic.Chunk;

public interface CellGetters {
  //== GETS ==//
  default int getCellIn(Chunk chunk, int x, int y) {
    return chunk.getDataIn(x, y, Chunk.CELL_ID);
  }
  default int getDeadlineIn(Chunk chunk, int x, int y) {
    return chunk.getDataIn(x, y, Chunk.CELL_DEADLINE);
  }
  default int getLastMovedIn(Chunk chunk, int x, int y) {
    return chunk.getDataIn(x, y, Chunk.CELL_LAST_MOVED);
  }
}
