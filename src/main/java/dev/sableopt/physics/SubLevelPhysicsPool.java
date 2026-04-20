package dev.sableopt.physics;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class SubLevelPhysicsPool {

    private final ForkJoinPool pool;
    private final int parallelism;

    public SubLevelPhysicsPool() {
        OptimizationsConfig config = SableOptimizations.getConfig();
        int requestedParallelism = config.physicsParallelism;
        if (requestedParallelism <= 0) {
            this.parallelism = Runtime.getRuntime().availableProcessors();
        } else {
            this.parallelism = Math.min(requestedParallelism, Runtime.getRuntime().availableProcessors());
        }
        this.pool = new ForkJoinPool(this.parallelism);
        SableOptimizations.LOGGER.info("Physics pool initialized with {} threads", this.parallelism);
    }

    public CompletableFuture<Void> schedulePhysics(List<Runnable> physicsTasks) {
        if (!SableOptimizations.getConfig().enableParallelPhysics || physicsTasks.size() <= 1) {
            for (Runnable task : physicsTasks) {
                task.run();
            }
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<?>[] futures = physicsTasks.stream()
            .map(task -> CompletableFuture.runAsync(task, pool))
            .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    public void shutdown() {
        pool.shutdown();
    }

    public int getParallelism() {
        return parallelism;
    }
}
