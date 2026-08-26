import settings.Config;
import controller.Director;
import model.universe.World;
import view.Movie;

public class Main {
  public static void main(String[] args) {
    World W = new World(Config.getInt("CHUNK_SIZE"), Config.getInt("WORLD_WIDTH"), Config.getInt("WORLD_HEIGHT"));
    Movie M = new Movie(W.width(), W.height());
    Director D = new Director(W, M);
    D.init();
  }
}
