package controller;

import model.logic.World;
import view.Movie;

public class Director {
  private final World WORLD;
  private final Movie MOVIE;

  public Director(World w, Movie m) {
    this.WORLD = w;
    this.MOVIE = m;
  }

  public void init() {
    WORLD.init();
    MOVIE.init();
    while (!MOVIE.shouldClose()) {
      step();
    }
  }

  public void step() {
    MOVIE.handleInput(WORLD);
    WORLD.step();
    MOVIE.step(WORLD);
  }
}
