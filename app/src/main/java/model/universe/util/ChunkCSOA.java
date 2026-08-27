package model.universe.util;
import model.universe.util.CSOA;
import model.universe.Chunk;

public class ChunkCSOA implements CSOA {
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
  public Chunk[] get()    { return this.DATA; }
  public int width()  { return this.WIDTH; }
  public int height() { return this.HEIGHT; }
  public int size()   { return this.SIZE; }
  //=======================================================================================



  //== PRIVATES ==//
  public int getIndex(int wx, int wy) {
    return wy * width() + wx;
  }
  public boolean inBounds(int wx, int wy) {
    return wx >= 0 && wx < width() && wy >= 0 && wy < height();
  }
  //=======================================================================================
  


  //== PUBLICS ==//
  public void setChunk(Chunk c, int i) { this.DATA[i] = c; }

  public Chunk getChunk(int i)         { return this.DATA[i]; }
  //=======================================================================================
}
