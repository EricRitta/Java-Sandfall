package util;

import java.util.List;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

public class ParallelProcessor {
  private static final int NUM_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
  private static final ExecutorService POOL = Executors.newFixedThreadPool(NUM_THREADS);
  private static final Phaser PHASER = new Phaser(1);

  private ParallelProcessor() {
    throw new UnsupportedOperationException("Don't try to instanciate this class.");
  }

  //== STATIC ==//
  public static synchronized <T> void forEach(List<T> items, Consumer<T> action) {
    int total = items.size();
    if (total == 0) { return; }

    int batches = Math.min(NUM_THREADS, total);
    int batchSize = (total + batches - 1) / batches;

    PHASER.bulkRegister(batches);

    for (int b = 0; b < batches; b++) {
      final int start = b * batchSize;
      final int end = Math.min(start + batchSize, total);

      POOL.execute(() -> {
        try {
          for (int i = start; i < end; i++) {
            action.accept(items.get(i));
          }
        } finally {
          PHASER.arriveAndDeregister();
        }
      });
    }

    PHASER.arriveAndAwaitAdvance();
  }

  public static synchronized <T> void forEach(T[] items, Consumer<T> action) {
    forEach(Arrays.asList(items), action);
  }

  public static void shutdown() {
    if (POOL != null) {
      POOL.shutdown();
    }
  }
}
