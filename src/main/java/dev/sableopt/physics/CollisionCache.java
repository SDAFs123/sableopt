package dev.sableopt.physics;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Vector3dc;

public class CollisionCache {

    private final Map<Long, CachedCollision> cache;
    private final Queue<Long> invalidations;
    private final int maxSize;

    private long cacheHits = 0;
    private long cacheMisses = 0;

    public CollisionCache() {
        OptimizationsConfig config = SableOptimizations.getConfig();
        this.maxSize = config.collisionCacheSize;
        this.cache = new HashMap<>(maxSize);
        this.invalidations = new ConcurrentLinkedQueue<>();
    }

    public CollisionResult query(Vector3dc position) {
        long key = encodePosition(position);
        CachedCollision cached = cache.get(key);

        if (cached != null && cached.isValid()) {
            cacheHits++;
            return cached.result;
        }

        cacheMisses++;
        return null;
    }

    public void store(Vector3dc position, CollisionResult result) {
        long key = encodePosition(position);
        if (cache.size() >= maxSize) {
            evictOldest();
        }
        cache.put(key, new CachedCollision(result, System.currentTimeMillis()));
    }

    public void invalidate(long key) {
        invalidations.offer(key);
    }

    public void invalidateRegion(long minX, long maxX) {
        for (Long key : cache.keySet()) {
            if (key >= minX && key <= maxX) {
                invalidations.offer(key);
            }
        }
    }

    public void flushInvalidations() {
        Long key;
        while ((key = invalidations.poll()) != null) {
            cache.remove(key);
        }
    }

    public double getHitRate() {
        long total = cacheHits + cacheMisses;
        if (total == 0) return 0;
        return (double) cacheHits / total;
    }

    public void resetStats() {
        cacheHits = 0;
        cacheMisses = 0;
    }

    private long encodePosition(Vector3dc pos) {
        int x = (int) Math.floor(pos.x() * 100);
        int y = (int) Math.floor(pos.y() * 100);
        int z = (int) Math.floor(pos.z() * 100);
        return ((long) x & 0xFFFF) | (((long) y & 0xFFFF) << 16) | (((long) z & 0xFFFF) << 32);
    }

    private void evictOldest() {
        if (cache.isEmpty()) return;
        long oldestKey = -1;
        long oldestTime = Long.MAX_VALUE;
        for (Map.Entry<Long, CachedCollision> entry : cache.entrySet()) {
            if (entry.getValue().timestamp < oldestTime) {
                oldestTime = entry.getValue().timestamp;
                oldestKey = entry.getKey();
            }
        }
        if (oldestKey != -1) {
            cache.remove(oldestKey);
        }
    }

    private record CachedCollision(CollisionResult result, long timestamp) {
        boolean isValid() {
            return System.currentTimeMillis() - timestamp < 5000;
        }
    }

    public record CollisionResult(int collisionType, Object colliderData) {}
}
