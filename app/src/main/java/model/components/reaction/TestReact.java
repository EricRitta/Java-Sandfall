package model.components.reaction;

import model.components.basics.*;
import model.universe.Chunk;

public interface TestReact extends Reaction {
  default boolean tryTestReact(Chunk chunk, int cx, int cy) {
    boolean success = false;
    System.out.println("Reagiu");
    return success;
  }
}
