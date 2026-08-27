package model.universe.util;
import model.universe.util.CSOA;

public class CellCSOA implements CSOA {
  private final int WIDTH;
  private final int HEIGHT;
  private final int SIZE;

  private final int[] ID;
  private final int[] DEADLINE;
  private final int[] LAST_UPDATED_FRAME;

  public CellCSOA(int w, int h) {
    this.WIDTH = w;
    this.HEIGHT = h;
    this.SIZE = WIDTH * HEIGHT;

    this.ID = new int[SIZE];
    this.DEADLINE = new int[SIZE];
    this.LAST_UPDATED_FRAME = new int[SIZE];
  }

  //== SETTERS ==//
  //=======================================================================================

  //== GETTERS ==//
  public int width()  { return this.WIDTH; }
  public int height() { return this.HEIGHT; }
  public int size()   { return this.SIZE; }
  //=======================================================================================
  
  

  //== PRIVATE ==//
  public int getIndex(int cx, int cy) {
    return cy * width() + cx;
  }
  public boolean inBounds(int cx, int cy) {
    return cx >= 0 && cx < width() && cy >= 0 && cy < height();
  }
  //=======================================================================================
  
  

  //== PUBLIC ==//
  public void setId(int value, int i)               { this.ID[i] = value; }
  public void setDeadline(int value, int i)         { this.DEADLINE[i] = value; }
  public void setLastUpdatedFrame(int value, int i) { this.LAST_UPDATED_FRAME[i] = value; }
  
  public int getId(int i)                           { return ID[i]; }
  public int getDeadline(int i)                     { return DEADLINE[i]; }
  public int getLastUpdatedFrame(int i)             { return LAST_UPDATED_FRAME[i]; }
  //=======================================================================================
}
