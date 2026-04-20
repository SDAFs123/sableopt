package dev.sableopt.render;

import dev.sableopt.SableOptimizations;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PoseUpdateBatcher {

    private final Queue<PoseUpdate> pendingUpdates;
    private final List<PoseUpdate> batchBuffer;
    private final int maxBatchSize = 64;

    private int totalUpdates = 0;
    private int batchedUpdates = 0;

    public PoseUpdateBatcher() {
        this.pendingUpdates = new ConcurrentLinkedQueue<>();
        this.batchBuffer = new ArrayList<>(maxBatchSize);
        SableOptimizations.LOGGER.debug("PoseUpdateBatcher initialized");
    }

    public void enqueue(int subLevelId, Matrix4f transform) {
        pendingUpdates.offer(new PoseUpdate(subLevelId, transform));
        totalUpdates++;
    }

    public void enqueue(int subLevelId, Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f transform = new Matrix4f()
            .translation(position)
            .rotateXYZ(rotation.x, rotation.y, rotation.z)
            .scale(scale);
        enqueue(subLevelId, transform);
    }

    public List<PoseUpdate> prepareBatch() {
        batchBuffer.clear();

        PoseUpdate update;
        while (batchBuffer.size() < maxBatchSize && (update = pendingUpdates.poll()) != null) {
            batchBuffer.add(update);
            batchedUpdates++;
        }

        return batchBuffer;
    }

    public void flush() {
        while (!pendingUpdates.isEmpty()) {
            List<PoseUpdate> batch = prepareBatch();
            if (!batch.isEmpty()) {
                uploadBatch(batch);
            }
        }
    }

    private void uploadBatch(List<PoseUpdate> batch) {
        // TODO: 实际的 GPU 上传逻辑
        // 这里是占位实现，实际需要调用渲染 API
        SableOptimizations.LOGGER.debug("Uploading {} pose updates", batch.size());
    }

    public int getPendingCount() {
        return pendingUpdates.size();
    }

    public double getBatchEfficiency() {
        if (totalUpdates == 0) return 0;
        return (double) batchedUpdates / totalUpdates;
    }

    public void resetStats() {
        totalUpdates = 0;
        batchedUpdates = 0;
    }

    public record PoseUpdate(int subLevelId, Matrix4f transform) {}
}
