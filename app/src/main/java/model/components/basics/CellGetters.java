package model.components.basics;

import model.universe.Chunk;

public interface CellGetters {
  //== GETS ==//
  default int getCellIn(Chunk chunk, int x, int y) {
    return chunk.getDataId(x, y);
  }
  default int getDeadlineIn(Chunk chunk, int x, int y) {
    return chunk.getDataDeadline(x, y);
  }
  default int getLastMovedIn(Chunk chunk, int x, int y) {
    return chunk.getDataLastUpdatedFrame(x, y);
  }
}
