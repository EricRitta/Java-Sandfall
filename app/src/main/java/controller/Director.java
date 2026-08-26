package controller;

import model.universe.World;
import view.Movie;

public class Director {
  private final World WORLD;
  private final Movie MOVIE;
  private boolean paused = false;

  private static final float FIXED_TIMESTAMP = 1.0f / 60.0f;
  private float accumulator = 0f;

  public void setPaused(boolean v) { this.paused = v; }
  public boolean isPaused() { return this.paused; }

  public Director(World w, Movie m) {
    this.WORLD = w;
    this.MOVIE = m;
  }

  public void init() {
    // WORLD.init();
    MOVIE.init();

    while (!MOVIE.shouldClose()) {
      MOVIE.handleInput(WORLD, this);

      if (!isPaused()) {

        float dt = MOVIE.getDt();
        accumulator += dt;

        while (accumulator >= FIXED_TIMESTAMP) {
          WORLD.step();
          accumulator -= FIXED_TIMESTAMP;
        }

      }

      MOVIE.step(WORLD);
    }

    // WORLD.shutdown();
  }

  public void step() {
    WORLD.step();
  }
}
