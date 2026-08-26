package model.extenders;

public class CellCSOA {
  private final int SIZE;
  private final int[] ID;
  private final int[] DEADLINE;
  private final int[] LAST_UPDATED_FRAME;

  public CellCSOA(int size) {
    this.SIZE = size;
    this.ID = new int[SIZE];
    this.DEADLINE = new int[SIZE];
    this.LAST_UPDATED_FRAME = new int[SIZE];
  }

  //== SETTERS ==//
  public void setId(int value, int i)               { this.ID[i] = value; }
  public void setDeadline(int value, int i)         { this.DEADLINE[i] = value; }
  public void setLastUpdatedFrame(int value, int i) { this.LAST_UPDATED_FRAME[i] = value; }
  //=======================================================================================

  //== GETTERS ==//
  public int size() { return this.SIZE; }
  //=======================================================================================
  
  

  //== PRIVATE ==//
  private int getIndex(int wx, int wy) {
    return wy * size() + wx;
  }
  //=======================================================================================
  
  

  //== PUBLIC ==//
  //=======================================================================================
}
