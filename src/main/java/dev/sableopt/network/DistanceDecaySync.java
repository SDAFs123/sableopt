package dev.sableopt.network;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import org.joml.Vector3dc;

public class DistanceDecaySync {

    private final OptimizationsConfig config;

    public DistanceDecaySync() {
        this.config = SableOptimizations.getConfig();
    }

    public int getSyncInterval(double distance) {
        for (int i = 0; i < config.distanceThresholds.length; i++) {
            if (distance < config.distanceThresholds[i]) {
                return config.syncIntervals[i];
            }
        }
        return config.syncIntervals[config.syncIntervals.length - 1];
    }

    public float getPositionPrecision(double distance) {
        for (int i = 0; i < config.distanceThresholds.length; i++) {
            if (distance < config.distanceThresholds[i]) {
                return config.positionPrecision[i];
            }
        }
        return config.positionPrecision[config.positionPrecision.length - 1];
    }

    public boolean shouldSyncThisTick(double distance, int currentTick) {
        int interval = getSyncInterval(distance);
        return currentTick % interval == 0;
    }

    public double getDistance(double playerX, double playerY, double playerZ, Vector3dc subLevelPos) {
        double dx = playerX - subLevelPos.x();
        double dy = playerY - subLevelPos.y();
        double dz = playerZ - subLevelPos.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
