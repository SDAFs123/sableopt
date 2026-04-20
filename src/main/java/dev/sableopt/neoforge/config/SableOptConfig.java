package dev.sableopt.neoforge.config;

import dev.sableopt.OptimizationsConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = "sableopt")
public class SableOptConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_PARALLEL_PHYSICS = BUILDER
        .comment("Enable parallel physics processing for multiple SubLevels")
        .define("physics.parallelPhysics", true);

    public static final ModConfigSpec.IntValue PHYSICS_PARALLELISM = BUILDER
        .comment("Physics thread pool parallelism (-1 for auto)")
        .defineInRange("physics.parallelism", -1, -1, 32);

    public static final ModConfigSpec.BooleanValue ENABLE_BATCH_BLOCK_UPDATES = BUILDER
        .comment("Enable batching of block updates before physics tick")
        .define("physics.batchBlockUpdates", true);

    public static final ModConfigSpec.IntValue BLOCK_UPDATE_BATCH_SIZE = BUILDER
        .comment("Maximum block updates per batch")
        .defineInRange("physics.blockUpdateBatchSize", 256, 16, 1024);

    public static final ModConfigSpec.BooleanValue ENABLE_POSE_BATCHING = BUILDER
        .comment("Enable batching of pose uploads to GPU")
        .define("render.poseBatching", true);

    public static final ModConfigSpec.BooleanValue ENABLE_RENDER_CACHE = BUILDER
        .comment("Enable render data caching for SubLevels")
        .define("render.renderCache", true);

    public static final ModConfigSpec.BooleanValue ENABLE_DISTANCE_DECAY = BUILDER
        .comment("Enable distance-based sync frequency decay")
        .define("network.distanceDecay", true);

    public static final ModConfigSpec.BooleanValue ENABLE_DELTA_SYNC = BUILDER
        .comment("Enable delta synchronization (only send changes)")
        .define("network.deltaSync", true);

    public static final ModConfigSpec.BooleanValue ENABLE_PRECISION_COMPRESSION = BUILDER
        .comment("Enable half-float compression for positions")
        .define("network.precisionCompression", true);

    public static final ModConfigSpec.BooleanValue ENABLE_CLIENT_PREDICTION = BUILDER
        .comment("Enable client-side pose prediction")
        .define("network.clientPrediction", true);

    public static final ModConfigSpec.BooleanValue ENABLE_SUBLEVEL_LOD = BUILDER
        .comment("Enable SubLevel LOD optimizations")
        .define("compat.sublevelLod", true);

    public static final ModConfigSpec.BooleanValue ENABLE_C2ME_COMPAT = BUILDER
        .comment("Enable C2ME compatibility detection")
        .define("compat.c2meCompat", true);

    public static final ModConfigSpec.BooleanValue LOG_PHYSICS_STATS = BUILDER
        .comment("Log physics optimization statistics")
        .define("debug.logPhysicsStats", false);

    public static final ModConfigSpec.BooleanValue LOG_NETWORK_STATS = BUILDER
        .comment("Log network optimization statistics")
        .define("debug.logNetworkStats", false);

    public static final ModConfigSpec CONFIG = BUILDER.build();

    private static OptimizationsConfig cachedConfig = null;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        cachedConfig = new OptimizationsConfig();
        cachedConfig.enableParallelPhysics = ENABLE_PARALLEL_PHYSICS.get();
        cachedConfig.physicsParallelism = PHYSICS_PARALLELISM.get();
        cachedConfig.enableBatchBlockUpdates = ENABLE_BATCH_BLOCK_UPDATES.get();
        cachedConfig.blockUpdateBatchSize = BLOCK_UPDATE_BATCH_SIZE.get();
        cachedConfig.enablePoseBatching = ENABLE_POSE_BATCHING.get();
        cachedConfig.enableRenderCache = ENABLE_RENDER_CACHE.get();
        cachedConfig.enableDistanceDecay = ENABLE_DISTANCE_DECAY.get();
        cachedConfig.enableDeltaSync = ENABLE_DELTA_SYNC.get();
        cachedConfig.enablePrecisionCompression = ENABLE_PRECISION_COMPRESSION.get();
        cachedConfig.enableClientPrediction = ENABLE_CLIENT_PREDICTION.get();
        cachedConfig.enableSublevelLod = ENABLE_SUBLEVEL_LOD.get();
        cachedConfig.enableC2MECompat = ENABLE_C2ME_COMPAT.get();
        cachedConfig.logPhysicsStats = LOG_PHYSICS_STATS.get();
        cachedConfig.logNetworkStats = LOG_NETWORK_STATS.get();
    }

    public static OptimizationsConfig getConfig() {
        if (cachedConfig == null) {
            cachedConfig = new OptimizationsConfig();
        }
        return cachedConfig;
    }
}
