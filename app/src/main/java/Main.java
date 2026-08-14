import model.logic.World;
import model.logic.Chunk;
import util.Config;

public class Main {
  public static void main(String[] args) {
    World w = new World(64, 2);
    Chunk c = w.getChunkOrNull(0, 0);
    c.setCellIn(0, 0, 1);
    int t = c.getCellIn(64, 63);
    System.out.println(Config.outOfBoundString(t));
  }
}
