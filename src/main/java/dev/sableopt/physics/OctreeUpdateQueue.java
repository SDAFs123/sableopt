package dev.sableopt.physics;

import dev.sableopt.SableOptimizations;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OctreeUpdateQueue {

    private final ConcurrentLinkedQueue<OctreeTask> queue;
    private final Thread workerThread;
    private volatile boolean running = true;

    public OctreeUpdateQueue() {
        this.queue = new ConcurrentLinkedQueue<>();
        this.workerThread = new Thread(this::processQueue, "SableOpt-OctreeWorker");
        this.workerThread.start();
        SableOptimizations.LOGGER.info("Octree update worker started");
    }

    public void enqueueInsert(int x, int y, int z, Object blockState) {
        queue.offer(new OctreeInsertTask(x, y, z, blockState));
    }

    public void enqueueRemove(int x, int y, int z) {
        queue.offer(new OctreeRemoveTask(x, y, z));
    }

    public void flushAndWait() {
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void shutdown() {
        running = false;
        workerThread.interrupt();
        try {
            workerThread.join(1000);
        } catch (InterruptedException e) {
            SableOptimizations.LOGGER.warn("Octree worker shutdown interrupted");
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    private void processQueue() {
        while (running) {
            OctreeTask task = queue.poll();
            if (task != null) {
                task.execute();
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    if (!running) break;
                }
            }
        }
    }

    private interface OctreeTask {
        void execute();
    }

    private record OctreeInsertTask(int x, int y, int z, Object blockState) implements OctreeTask {
        @Override
        public void execute() {
        }
    }

    private record OctreeRemoveTask(int x, int y, int z) implements OctreeTask {
        @Override
        public void execute() {
        }
    }
}
