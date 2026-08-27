package controller;

import util.Config;
import model.universe.World;
import view.Movie;

public class Director {
  private final World WORLD = new World(
    Config.getInt("CHUNK_SIZE"),
    Config.getInt("WORLD_WIDTH"),
    Config.getInt("WORLD_HEIGHT")
  );

  private final Movie MOVIE = new Movie(
    Config.getInt("WORLD_WIDTH"),
    Config.getInt("WORLD_HEIGHT")
  );

  private boolean paused = false;

  private static final float FIXED_TIMESTAMP = 1.0f / 60.0f;
  private float accumulator = 0f;

  public void setPaused(boolean v) { this.paused = v; }
  public boolean isPaused() { return this.paused; }

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

    WORLD.shutdown();
  }

  public void step() {
    WORLD.step();
  }
}
