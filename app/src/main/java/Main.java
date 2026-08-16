import util.Config;
import controller.Director;
import model.logic.World;
import view.Movie;

public class Main {
  public static void main(String[] args) {
    World W = new World(Config.getInt("CHUNK_SIZE"), Config.getInt("WORLD_SIZE"));
    Movie M = new Movie(W.getWorldWidth(), W.getWorldHeight());
    Director D = new Director(W, M);
    D.init();
  }
}
