package dev.sableopt.physics;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicsResultCollector {

    private final ConcurrentLinkedQueue<PhysicsResult> results = new ConcurrentLinkedQueue<>();

    public void collect(int subLevelId, PhysicsResultType type, Object data) {
        results.offer(new PhysicsResult(subLevelId, type, data));
    }

    public List<PhysicsResult> getAllResults() {
        return List.copyOf(results);
    }

    public void clear() {
        results.clear();
    }

    public int size() {
        return results.size();
    }

    public record PhysicsResult(int subLevelId, PhysicsResultType type, Object data) {}

    public enum PhysicsResultType {
        COLLISION_RESPONSE,
        VELOCITY_CHANGE,
        POSITION_CHANGE
    }
}
