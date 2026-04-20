package dev.sableopt;

public class OptimizationsConfig {
    // Network optimization
    public boolean enablePrecisionCompression = true;
    public boolean enableDeltaSync = true;
    public boolean enableDistanceDecay = true;
    public boolean enableClientPrediction = true;

    // Distance decay config
    public int[] distanceThresholds = {64, 128, 256};
    public int[] syncIntervals = {1, 2, 4};
    public float[] positionPrecision = {0.001f, 0.01f, 0.1f};

    // Physics optimization
    public boolean enableParallelPhysics = true;
    public int physicsParallelism = -1;
    public boolean enableCollisionCache = true;
    public int collisionCacheSize = 10000;
    public boolean enableAsyncOctree = true;
    public boolean enableBatchBlockUpdates = true;
    public int blockUpdateBatchSize = 256;

    // Render optimization
    public boolean enablePoseBatching = true;
    public boolean enableRenderCache = true;

    // SubLevel LOD optimization
    public boolean enableSublevelLod = true;
    public boolean autoDetectSable = true;

    // C2ME compat
    public boolean enableC2MECompat = true;
    public boolean c2meAutoDetect = true;

    // Debug
    public boolean enableProfiler = false;
    public boolean logPhysicsStats = false;
    public boolean logNetworkStats = false;

    public OptimizationsConfig() {
    }
}
