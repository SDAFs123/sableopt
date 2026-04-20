package dev.sableopt.voxy;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import dev.sableopt.voxy.SubLevelLODRenderer.LODMesh;
import java.util.HashMap;
import java.util.Map;

public class VoxyManager {

    private static volatile VoxyManager instance;

    private final VoxyDetector detector;
    private final SubLevelLODRenderer lodRenderer;
    private final SubLevelLODBuilder lodBuilder;
    private final DynamicLODUpdater lodUpdater;

    private final Map<Integer, LODMesh> lodStorage;

    private volatile boolean initialized = false;

    public static VoxyManager getInstance() {
        if (instance == null) {
            synchronized (VoxyManager.class) {
                if (instance == null) {
                    instance = new VoxyManager();
                }
            }
        }
        return instance;
    }

    private VoxyManager() {
        OptimizationsConfig config = SableOptimizations.getConfig();

        if (!config.enableSublevelLod) {
            this.detector = null;
            this.lodRenderer = null;
            this.lodBuilder = null;
            this.lodUpdater = null;
            this.lodStorage = null;
            SableOptimizations.LOGGER.info("Voxy compatibility disabled");
            return;
        }

        this.detector = new VoxyDetector();
        if (config.autoDetectSable) {
            VoxyDetector.detect();
        }

        this.lodRenderer = new SubLevelLODRenderer();
        this.lodBuilder = new SubLevelLODBuilder();
        this.lodUpdater = new DynamicLODUpdater();
        this.lodStorage = new HashMap<>();

        this.initialized = true;
        SableOptimizations.LOGGER.info("VoxyManager initialized, Voxy loaded: {}", VoxyDetector.isVoxyLoaded());
    }

    public boolean shouldUseSubLevelLOD() {
        return initialized && VoxyDetector.isVoxyLoaded();
    }

    public void renderSubLevelLOD(int subLevelId, Object plot, Object pose) {
        if (lodRenderer != null) {
            lodRenderer.render(subLevelId, plot, pose);
        }
    }

    public void updateLODPosition(int subLevelId, Object newPose) {
        if (lodRenderer != null) {
            lodRenderer.updatePosition(subLevelId, newPose);
        }
    }

    public void removeSubLevelLOD(int subLevelId) {
        if (lodRenderer != null) {
            lodRenderer.remove(subLevelId);
        }
        if (lodStorage != null) {
            lodStorage.remove(subLevelId);
        }
    }

    public void logStats() {
        if (!initialized || !SableOptimizations.getConfig().logNetworkStats) {
            return;
        }

        SableOptimizations.LOGGER.info("Voxy stats: rendered {} LODs, cache size {}, built {}",
            lodRenderer != null ? lodRenderer.getRenderedCount() : 0,
            lodRenderer != null ? lodRenderer.getCacheSize() : 0,
            lodBuilder != null ? lodBuilder.getBuiltCount() : 0);
    }

    public VoxyDetector getDetector() {
        return detector;
    }

    public SubLevelLODRenderer getLodRenderer() {
        return lodRenderer;
    }

    public SubLevelLODBuilder getLodBuilder() {
        return lodBuilder;
    }

    public DynamicLODUpdater getLodUpdater() {
        return lodUpdater;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
