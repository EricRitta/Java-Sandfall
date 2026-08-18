package model.components.basics;
import model.components.basics.*;
import model.logic.Chunk;

public interface Reaction extends Getters {
  int getId();
  String getType();
    
  //== SETS ==//
  private void setCellIn(Chunk chunk, int cx, int cy, int id, int deadline) {
    chunk.setDataPointIn(cx, cy, id, deadline);
  }
  private void setCellIn(Chunk chunk, int cx, int cy, int id, int deadline, int lastMoved) {
    chunk.setDataPointIn(cx, cy, id, deadline, lastMoved);
  }
  private void resetCellIn(Chunk chunk, int cx, int cy) {
    chunk.resetDataPointIn(cx, cy);
  }
}
