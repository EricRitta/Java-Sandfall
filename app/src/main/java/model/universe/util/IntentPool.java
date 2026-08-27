package model.universe.util;
import model.universe.util.Intent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class IntentPool {
  private static final int INITIAL_VALUE = 4096;
  private static final ConcurrentLinkedQueue<Intent> POOL = new ConcurrentLinkedQueue<>();
  static {
    for (int i = 0; i < INITIAL_VALUE; i++) {
      POOL.offer(new Intent());
    }
  }

  public static Intent get() {
    Intent intent = POOL.poll();
    if (intent == null) {
      return new Intent();
    }
    return intent;
  }

  public static void free(Intent intent) {
    if (intent == null) { return; }
    intent.clear();
    POOL.offer(intent);
  }
}
