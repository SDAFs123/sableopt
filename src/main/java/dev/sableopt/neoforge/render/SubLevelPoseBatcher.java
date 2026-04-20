package dev.sableopt.neoforge.render;

import dev.sableopt.SableOptimizations;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;

public class SubLevelPoseBatcher {

    private static volatile SubLevelPoseBatcher instance;

    private final Queue<PoseEntry> pendingEntries;
    private final List<PoseEntry> batchBuffer;
    private final int maxBatchSize = 64;

    private int totalEnqueued = 0;
    private int batchUploads = 0;
    private boolean frameActive = false;

    public static SubLevelPoseBatcher getInstance() {
        if (instance == null) {
            synchronized (SubLevelPoseBatcher.class) {
                if (instance == null) {
                    instance = new SubLevelPoseBatcher();
                }
            }
        }
        return instance;
    }

    private SubLevelPoseBatcher() {
        this.pendingEntries = new ConcurrentLinkedQueue<>();
        this.batchBuffer = new ArrayList<>(maxBatchSize);
        SableOptimizations.LOGGER.debug("SubLevelPoseBatcher initialized");
    }

    public void beginFrame() {
        this.frameActive = true;
        pendingEntries.clear();
    }

    public void enqueue(int subLevelId, Vector3dc position, Quaternionfc orientation) {
        if (!frameActive) return;

        Matrix4f transform = new Matrix4f();
        transform.identity();
        transform.translate((float) position.x(), (float) position.y(), (float) position.z());
        transform.rotate(orientation);
        pendingEntries.offer(new PoseEntry(subLevelId, transform));
        totalEnqueued++;
    }

    public void endFrame() {
        flushPending();
        this.frameActive = false;
    }

    private void flushPending() {
        while (!pendingEntries.isEmpty()) {
            batchBuffer.clear();
            PoseEntry entry;
            while (batchBuffer.size() < maxBatchSize && (entry = pendingEntries.poll()) != null) {
                batchBuffer.add(entry);
            }

            if (!batchBuffer.isEmpty()) {
                uploadBatch(batchBuffer);
                batchUploads++;
            }
        }
    }

    private void uploadBatch(List<PoseEntry> batch) {
        SableOptimizations.LOGGER.debug("Batch uploaded {} pose transforms", batch.size());
    }

    public int getPendingCount() {
        return pendingEntries.size();
    }

    public double getBatchEfficiency() {
        if (totalEnqueued == 0) return 0;
        return (double) batchUploads * maxBatchSize / totalEnqueued;
    }

    public void resetStats() {
        totalEnqueued = 0;
        batchUploads = 0;
    }

    public record PoseEntry(int subLevelId, Matrix4f transform) {}
}
