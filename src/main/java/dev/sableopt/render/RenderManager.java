package dev.sableopt.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import org.joml.Matrix4f;

public class RenderManager {

    private static volatile RenderManager instance;

    private final PoseUpdateBatcher poseBatcher;
    private final SubLevelRenderCache renderCache;

    private volatile boolean initialized = false;

    public static RenderManager getInstance() {
        if (instance == null) {
            synchronized (RenderManager.class) {
                if (instance == null) {
                    instance = new RenderManager();
                }
            }
        }
        return instance;
    }

    private RenderManager() {
        OptimizationsConfig config = SableOptimizations.getConfig();

        this.poseBatcher = config.enablePoseBatching ? new PoseUpdateBatcher() : null;
        this.renderCache = config.enableRenderCache ? new SubLevelRenderCache() : null;

        this.initialized = true;
        SableOptimizations.LOGGER.info("RenderManager initialized");
    }

    public void beforeRender() {
        if (poseBatcher != null) {
            poseBatcher.flush();
        }
    }

    public void afterRender() {
        if (poseBatcher != null && SableOptimizations.getConfig().logNetworkStats) {
            SableOptimizations.LOGGER.info("Pose batch efficiency: {}%",
                String.format("%.2f", poseBatcher.getBatchEfficiency() * 100));
        }
        if (renderCache != null && SableOptimizations.getConfig().logNetworkStats) {
            SableOptimizations.LOGGER.info("Render cache hit rate: {}%",
                String.format("%.2f", renderCache.getHitRate() * 100));
        }
    }

    public void enqueuePoseUpdate(int subLevelId, Object pose) {
        if (poseBatcher != null && pose != null) {
            try {
                if (pose instanceof com.mojang.blaze3d.vertex.PoseStack poseStack) {
                    Matrix4f transform = new Matrix4f();
                    poseStack.last().pose().get(transform);
                    poseBatcher.enqueue(subLevelId, transform);
                } else if (pose instanceof org.joml.Matrix4f matrix) {
                    poseBatcher.enqueue(subLevelId, matrix);
                }
            } catch (Exception e) {
                SableOptimizations.LOGGER.warn("Failed to extract pose for SubLevel {}", subLevelId);
            }
        }
    }

    public SubLevelRenderCache.RenderData getRenderData(int subLevelId,
                                                         SubLevelRenderCache.RenderDataBuilder builder) {
        if (renderCache != null) {
            return renderCache.getOrCreate(subLevelId, builder);
        }
        return builder.build();
    }

    public void markRenderDataDirty(int subLevelId) {
        if (renderCache != null) {
            renderCache.markDirty(subLevelId);
        }
    }

    public void removeRenderData(int subLevelId) {
        if (renderCache != null) {
            renderCache.remove(subLevelId);
        }
    }

    public PoseUpdateBatcher getPoseBatcher() {
        return poseBatcher;
    }

    public SubLevelRenderCache getRenderCache() {
        return renderCache;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
