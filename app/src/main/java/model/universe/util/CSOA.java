package model.universe.util;

public interface CSOA {
  int width();
  int height();
  int size();

  int getIndex(int tx, int ty);
  boolean inBounds(int tx, int ty);
}
