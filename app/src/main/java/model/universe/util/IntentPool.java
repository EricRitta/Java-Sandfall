package model.universe.util;
import model.universe.util.Intent;

import java.util.ArrayDeque;
import java.lang.ThreadLocal;

public class IntentPool {
  //== CONSTANTS ==//
  private static final ThreadLocal<ArrayDeque<Intent>> LOCAL_POOL = new ThreadLocal<>();

  private static ArrayDeque<Intent> getPool() {
    ArrayDeque<Intent> pool = LOCAL_POOL.get();
    if (pool == null) {
      pool = new ArrayDeque<Intent>();
      LOCAL_POOL.set(pool);
    }
    return pool;
  }

  public static Intent get() {
    ArrayDeque<Intent> pool = getPool();
    if (pool.isEmpty()) {
      return new Intent();
    }
    return pool.pop();
  }

  public static void free(Intent intent) {
    if (intent == null) { return; }
    ArrayDeque<Intent> pool = getPool();
    intent.clear();
    pool.push(intent);
  }
}
