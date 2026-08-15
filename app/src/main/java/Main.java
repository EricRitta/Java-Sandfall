import model.logic.World;
import model.logic.Chunk;
import util.Config;

public class Main {
  public static void main(String[] args) {
    World w = new World(64, 2);
    Chunk c = w.getChunk(0, 0);
    c.setDataPointIn(0, 0, 1, 0);
    int t = c.getDataPointIn(64, 63, Config.getInt("CELL_FIELD"));
    System.out.println(Config.outOfBoundString(t));
  }
}
