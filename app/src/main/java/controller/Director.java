package controller;

import model.logic.World;
import view.Movie;

public class Director {
  private final World WORLD;
  private final Movie MOVIE;
  private boolean paused = false;

  public void setPaused(boolean v) { this.paused = v; }
  public boolean isPaused() { return this.paused; }

  public Director(World w, Movie m) {
    this.WORLD = w;
    this.MOVIE = m;
  }

  public void init() {
    WORLD.init();
    MOVIE.init();
    while (!MOVIE.shouldClose()) {
      MOVIE.handleInput(WORLD, this);
      if (!isPaused()) {
        WORLD.step();
      }
      MOVIE.step(WORLD);
    }
    WORLD.shutdown();
  }

  public void step() {
    WORLD.step();
  }
}
