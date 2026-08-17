package model.behaviours.interfaces;

import util.Config;
import model.logic.Chunk;

public interface BehaviourHelpers {
  // CELL SETTERS //
  default void setCellIn(Chunk chunk, int cx, int cy, int id, int deadline) {
    chunk.setDataPointIn(cx, cy, id, deadline);
  }
  default void setCellIn(Chunk chunk, int cx, int cy, int id, int deadline, int lastMoved) {
    chunk.setDataPointIn(cx, cy, id, deadline, lastMoved);
  }
  default void resetCellIn(Chunk chunk, int cx, int cy) {
    chunk.resetDataPointIn(cx, cy);
  }
  
  // CELL GETTERS //
  default int getCellIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Config.getInt("CELL_FIELD"));
  }
  default int getDeadlineIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Config.getInt("CELL_DEADLINE_FIELD"));
  }
  default int getLastMovedIn(Chunk chunk, int cx, int cy) {
    return chunk.getDataPointIn(cx, cy, Config.getInt("CELL_LAST_MOVED_FIELD"));
  }
}
