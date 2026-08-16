import controller.Director;
import model.logic.World;
import view.Movie;

public class Main {
  public static void main(String[] args) {
    World W = new World(64, 2);
    Movie M = new Movie(W.getWorldWidth(), W.getWorldHeight());
    Director D = new Director(W, M);
    D.init();
  }
}
