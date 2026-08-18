package model.components.basics;

import model.logic.Chunk;

public interface Getters {
  //== GETS ==//
  default int getCellIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Chunk.CELL_ID);
  }
  default int getDeadlineIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Chunk.CELL_DEADLINE);
  }
  default int getLastMovedIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Chunk.CELL_LAST_MOVED);
  }
}
