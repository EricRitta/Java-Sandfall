package model.logic;

public class DirtyRect {
  private int CHUNK_SIZE;
  private int maxCX = Integer.MIN_VALUE;
  private int maxCY = Integer.MIN_VALUE;
  private int minCX = Integer.MAX_VALUE;
  private int minCY = Integer.MAX_VALUE;
  private boolean isEmpty = true;

  public DirtyRect(int chunkSize) {
    this.CHUNK_SIZE = chunkSize;
  }
 
  //== GETTERS ==//
  public int getMaxCX() { return this.maxCX; }
  public int getMaxCY() { return this.maxCY; }
  public int getMinCX() { return this.minCX; }
  public int getMinCY() { return this.minCY; }
  public boolean getIsEmpty() { return this.isEmpty; }
  //=======================================================================================

  // PRIVATES //
  private void expandBounds(int cx, int cy) {
    cx = clamp(cx, 0, CHUNK_SIZE - 1);
    cy = clamp(cy, 0, CHUNK_SIZE - 1);

    if (cx > maxCX) { maxCX = cx; }
    if (cy > maxCY) { maxCY = cy; }
    if (cx < minCX) { minCX = cx; }
    if (cy < minCY) { minCY = cy; }
    isEmpty = false;
  }

  private int clamp(int value, int min, int max) {
    if (value > max) { return max; }
    if (value < min) { return min; }
    return value;
  }
  //=======================================================================================

  // PACKAGE PRIVATES //
  void makeDirty(int cx, int cy) {
    expandBounds(cx + 1, cy + 1);
    expandBounds(cx - 1, cy - 1);
  }

  void clear() {
    maxCX = Integer.MIN_VALUE;
    maxCY = Integer.MIN_VALUE;
    minCX = Integer.MAX_VALUE;
    minCY = Integer.MAX_VALUE;
    isEmpty = true;
  }
  //=======================================================================================
}
