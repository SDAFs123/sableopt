package dev.sableopt.neoforge.network;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3dc;

public class DistanceDecayCalculator {

    private final int[] distanceThresholds;
    private final int[] syncIntervals;
    private final Map<Integer, Integer> subLevelTickCounter;
    private int filteredCount = 0;

    public DistanceDecayCalculator() {
        OptimizationsConfig config = SableOptimizations.getConfig();
        this.distanceThresholds = config.distanceThresholds;
        this.syncIntervals = config.syncIntervals;
        this.subLevelTickCounter = new HashMap<>();
    }

    public void beginTick() {
        filteredCount = 0;
    }

    public boolean shouldSendUpdate(ServerPlayer player, Vector3dc subLevelPos, int subLevelId) {
        double distance = sqrt(
            square(player.getX() - subLevelPos.x()) +
            square(player.getY() - subLevelPos.y()) +
            square(player.getZ() - subLevelPos.z())
        );

        int syncInterval = getSyncInterval(distance);

        int currentTick = subLevelTickCounter.getOrDefault(subLevelId, 0);
        subLevelTickCounter.put(subLevelId, currentTick + 1);

        if (currentTick % syncInterval != 0) {
            filteredCount++;
            return false;
        }

        return true;
    }

    public int getSyncInterval(double distance) {
        for (int i = 0; i < distanceThresholds.length; i++) {
            if (distance < distanceThresholds[i]) {
                return syncIntervals[i];
            }
        }
        return syncIntervals[syncIntervals.length - 1];
    }

    public int getFilteredCount() {
        return filteredCount;
    }

    public void reset() {
        subLevelTickCounter.clear();
        filteredCount = 0;
    }

    private double square(double x) {
        return x * x;
    }

    private double sqrt(double x) {
        return java.lang.Math.sqrt(x);
    }
}
