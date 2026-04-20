package dev.sableopt.voxy;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sableopt.SableOptimizations;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;

public class SubLevelLODRenderer {

    private final SubLevelLODBuilder lodBuilder;
    private final DynamicLODUpdater lodUpdater;
    private final Map<Integer, LODMesh> lodCache;

    private int renderedLODs = 0;

    public SubLevelLODRenderer() {
        this.lodBuilder = new SubLevelLODBuilder();
        this.lodUpdater = new DynamicLODUpdater();
        this.lodCache = new HashMap<>();
        SableOptimizations.LOGGER.debug("SubLevelLODRenderer initialized");
    }

    public void render(int subLevelId, Object plot, Object pose) {
        if (!SableOptimizations.getConfig().enableSublevelLod) {
            return;
        }

        LODMesh lod = lodCache.get(subLevelId);

        if (lod == null) {
            lod = lodBuilder.build(plot);
            lodCache.put(subLevelId, lod);
        }

        Matrix4f transform = extractTransform(pose);
        lodUpdater.update(lod, transform);
        renderedLODs++;
    }

    public void updatePosition(int subLevelId, Object newPose) {
        LODMesh lod = lodCache.get(subLevelId);
        if (lod != null) {
            Matrix4f transform = extractTransform(newPose);
            lodUpdater.update(lod, transform);
        }
    }

    public void remove(int subLevelId) {
        lodCache.remove(subLevelId);
    }

    public void clear() {
        lodCache.clear();
        renderedLODs = 0;
    }

    public int getRenderedCount() {
        return renderedLODs;
    }

    public int getCacheSize() {
        return lodCache.size();
    }

    private Matrix4f extractTransform(Object pose) {
        if (pose == null) {
            return new Matrix4f();
        }
        
        Matrix4f transform = new Matrix4f();
        
        try {
            if (pose instanceof PoseStack poseStack) {
                poseStack.last().pose().get(transform);
            } else if (pose instanceof Matrix4f matrix) {
                transform.set(matrix);
            }
        } catch (Exception e) {
            SableOptimizations.LOGGER.warn("Failed to extract transform from pose: {}", e.getMessage());
        }
        
        return transform;
    }

    public static class LODMesh {
        private final int vertexCount;
        private final int faceCount;
        private final Object meshData;
        private Matrix4f currentTransform;

        public LODMesh(int vertexCount, int faceCount, Object meshData) {
            this.vertexCount = vertexCount;
            this.faceCount = faceCount;
            this.meshData = meshData;
            this.currentTransform = new Matrix4f();
        }

        public int getVertexCount() {
            return vertexCount;
        }

        public int getFaceCount() {
            return faceCount;
        }

        public Object getMeshData() {
            return meshData;
        }

        public Matrix4f getCurrentTransform() {
            return currentTransform;
        }

        public void setTransform(Matrix4f transform) {
            this.currentTransform = transform;
        }
    }
}
