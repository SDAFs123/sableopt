package dev.sableopt.render;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import java.util.HashMap;
import java.util.Map;

public class SubLevelRenderCache {

    private final Map<Integer, RenderData> cache;
    private final Map<Integer, Long> lastUpdateTime;

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public SubLevelRenderCache() {
        OptimizationsConfig config = SableOptimizations.getConfig();
        this.cache = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        SableOptimizations.LOGGER.debug("SubLevelRenderCache initialized");
    }

    public RenderData getOrCreate(int subLevelId, RenderDataBuilder builder) {
        RenderData cached = cache.get(subLevelId);

        if (cached != null && !cached.isDirty()) {
            cacheHits++;
            return cached;
        }

        cacheMisses++;
        RenderData newData = builder.build();
        cache.put(subLevelId, newData);
        lastUpdateTime.put(subLevelId, System.currentTimeMillis());
        return newData;
    }

    public void markDirty(int subLevelId) {
        RenderData data = cache.get(subLevelId);
        if (data != null) {
            data.markDirty();
        }
    }

    public void remove(int subLevelId) {
        cache.remove(subLevelId);
        lastUpdateTime.remove(subLevelId);
    }

    public void clear() {
        cache.clear();
        lastUpdateTime.clear();
    }

    public int size() {
        return cache.size();
    }

    public double getHitRate() {
        int total = cacheHits + cacheMisses;
        if (total == 0) return 0;
        return (double) cacheHits / total;
    }

    public void resetStats() {
        cacheHits = 0;
        cacheMisses = 0;
    }

    public static class RenderData {
        private Object meshData;
        private Object materialData;
        private boolean dirty = false;

        public RenderData(Object meshData, Object materialData) {
            this.meshData = meshData;
            this.materialData = materialData;
        }

        public Object getMeshData() {
            return meshData;
        }

        public Object getMaterialData() {
            return materialData;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void markDirty() {
            this.dirty = true;
        }

        public void markClean() {
            this.dirty = false;
        }

        public void updateMesh(Object newMeshData) {
            this.meshData = newMeshData;
            this.dirty = true;
        }

        public void updateMaterial(Object newMaterialData) {
            this.materialData = newMaterialData;
            this.dirty = true;
        }
    }

    public interface RenderDataBuilder {
        RenderData build();
    }
}
