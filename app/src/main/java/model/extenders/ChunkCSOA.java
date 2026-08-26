package model.extenders;
import model.universe.Chunk;

public class ChunkCSOA {
  private final int WIDTH;
  private final int HEIGHT;
  private final int SIZE;
  private final Chunk DATA[];
  
  public ChunkCSOA(int w, int h) {
    this.WIDTH = w;
    this.HEIGHT = h;
    this.SIZE = WIDTH * HEIGHT;
    this.DATA = new Chunk[SIZE];
  }

  //== SETTERS ==//
  //== GETTERS ==//
  public int width()  { return this.WIDTH; }
  public int height() { return this.HEIGHT; }
  public int size()   { return this.SIZE; }
  //=======================================================================================



  //== PRIVATES ==//
  private int getIndex(int wx, int wy) {
    return wy * width() + wx;
  }
  //=======================================================================================
  


  //== PUBLICS ==//
  public Chunk getChunk(int wx, int wy) {
    return getChunkAtIndex(getIndex(wx, wy));
  }

  public void setChunkAtIndex(Chunk c, int i) {
    DATA[i] = c;
  }
  public Chunk getChunkAtIndex(int i) {
    return DATA[i];
  }
  //=======================================================================================
}
