package dev.sableopt.physics;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;
import java.util.List;

public class PhysicsManager {

    private static volatile PhysicsManager instance;

    private final SubLevelPhysicsPool physicsPool;
    private final CollisionCache collisionCache;
    private final OctreeUpdateQueue octreeQueue;
    private final PhysicsResultCollector resultCollector;

    private volatile boolean initialized = false;

    public static PhysicsManager getInstance() {
        if (instance == null) {
            synchronized (PhysicsManager.class) {
                if (instance == null) {
                    instance = new PhysicsManager();
                }
            }
        }
        return instance;
    }

    private PhysicsManager() {
        OptimizationsConfig config = SableOptimizations.getConfig();

        this.physicsPool = config.enableParallelPhysics ? new SubLevelPhysicsPool() : null;
        this.collisionCache = config.enableCollisionCache ? new CollisionCache() : null;
        this.octreeQueue = config.enableAsyncOctree ? new OctreeUpdateQueue() : null;
        this.resultCollector = new PhysicsResultCollector();

        this.initialized = true;
        SableOptimizations.LOGGER.info("PhysicsManager initialized");
    }

    public void tickPhysics(List<Runnable> physicsTasks) {
        if (physicsPool != null) {
            physicsPool.schedulePhysics(physicsTasks).join();
        } else {
            for (Runnable task : physicsTasks) {
                task.run();
            }
        }

        if (collisionCache != null) {
            collisionCache.flushInvalidations();
        }

        if (octreeQueue != null) {
            octreeQueue.flushAndWait();
        }

        resultCollector.clear();
    }

    public void shutdown() {
        if (physicsPool != null) {
            physicsPool.shutdown();
        }
        if (octreeQueue != null) {
            octreeQueue.shutdown();
        }
    }

    public SubLevelPhysicsPool getPhysicsPool() {
        return physicsPool;
    }

    public CollisionCache getCollisionCache() {
        return collisionCache;
    }

    public OctreeUpdateQueue getOctreeQueue() {
        return octreeQueue;
    }

    public PhysicsResultCollector getResultCollector() {
        return resultCollector;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
