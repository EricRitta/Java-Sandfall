package model.logic;

public class World {
  private int CHUNK_SIZE = 64;
  private int BOX_SIZE = 8; // 512 x 512 celulas, ou seja 250k+ celulas
  private int WORLD_SIZE = BOX_SIZE * CHUNK_SIZE;
  private int time = 0;

  private Chunk[] data = new Chunk[BOX_SIZE * BOX_SIZE];

  // public World(int cs, int ws) {
  //   this.CHUNK_SIZE = cs;
  //   this.WORLD_SIZE = ws;
  //   data = new int[WORLD_SIZE * WORLD_SIZE * FIELDS];
  // }



  // IMPORTANT PRIVATES //
  private void inWorldBounds(int x, int y) {
    if (x >= 0 && x < WORLD_SIZE && y >= 0 && y < WORLD_SIZE) {
      throw new IllegalArgumentException(
        "Position out of bounds in a " + WORLD_SIZE + "world size: (" + x + ", " + y + ")" 
      );
    }
  }
  private int worldIndex(int x, int y) {

  }
  private int index(int x, int y) {
    inBounds(x, y);
    return (y * CHUNK_SIZE + x) * FIELDS;
  }
  //==============================================================================================================


  void getWorldIndexByCord(int x, int y) {
      
  }


  // SETTERS //
  public void setId(int x, int y, int value) {

    data[index(x, y) + ID] = value; 
  }
  //==============================================================================================================



  // GETTERS //
  public int getTime() { return this.time; }
  public int getCHUNK_SIZE() { return this.CHUNK_SIZE; }
  //==============================================================================================================
}
