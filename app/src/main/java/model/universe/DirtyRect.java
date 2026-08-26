package model.universe;

public class DirtyRect {
  private int SIZE;
  private int maxCX = Integer.MIN_VALUE;
  private int maxCY = Integer.MIN_VALUE;
  private int minCX = Integer.MAX_VALUE;
  private int minCY = Integer.MAX_VALUE;
  private boolean empty = true;

  public DirtyRect(int Size) {
    this.SIZE = Size;
  }
 
  //== GETTERS ==//
  public int     maxCX()   { return this.maxCX; }
  public int     maxCY()   { return this.maxCY; }
  public int     minCX()   { return this.minCX; }
  public int     minCY()   { return this.minCY; }
  public boolean isEmpty() { return this.empty; }
  //=======================================================================================

  // PRIVATES //
  private void expandBounds(int cx, int cy) {
    cx = clamp(cx, 0, SIZE - 1);
    cy = clamp(cy, 0, SIZE - 1);

    if (cx > maxCX) { maxCX = cx; }
    if (cy > maxCY) { maxCY = cy; }
    if (cx < minCX) { minCX = cx; }
    if (cy < minCY) { minCY = cy; }
    empty = false;
  }

  private int clamp(int value, int min, int max) {
    if (value > max) { return max; }
    if (value < min) { return min; }
    return value;
  }
  //=======================================================================================

  // PACKAGE PRIVATES //
  public void makeDirty(int cx, int cy) {
    expandBounds(cx + 2, cy + 2);
    expandBounds(cx - 2, cy - 2);
  }

  public void clear() {
    maxCX = Integer.MIN_VALUE;
    maxCY = Integer.MIN_VALUE;
    minCX = Integer.MAX_VALUE;
    minCY = Integer.MAX_VALUE;
    empty = true;
  }
  //=======================================================================================
}
