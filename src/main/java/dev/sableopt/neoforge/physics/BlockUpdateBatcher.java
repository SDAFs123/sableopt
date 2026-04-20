package dev.sableopt.neoforge.physics;

import dev.sableopt.SableOptimizations;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class BlockUpdateBatcher {

    private final ConcurrentLinkedQueue<PendingUpdate> pendingUpdates;
    private final int maxBatchSize;

    public BlockUpdateBatcher() {
        this.pendingUpdates = new ConcurrentLinkedQueue<>();
        this.maxBatchSize = SableOptimizations.getConfig().blockUpdateBatchSize;
    }

    public void addUpdate(SectionPos sectionPos, LevelChunkSection section, int localX, int localY, int localZ, BlockState oldState, BlockState newState) {
        PendingUpdate update = new PendingUpdate(sectionPos, section, localX, localY, localZ, oldState, newState);
        pendingUpdates.offer(update);
    }

    public List<PendingUpdate> flush() {
        List<PendingUpdate> batch = new ArrayList<>();
        PendingUpdate update;
        while ((update = pendingUpdates.poll()) != null && batch.size() < maxBatchSize) {
            batch.add(update);
        }
        return batch;
    }

    public boolean isEmpty() {
        return pendingUpdates.isEmpty();
    }

    public int getPendingCount() {
        return pendingUpdates.size();
    }

    public record PendingUpdate(
        SectionPos sectionPos,
        LevelChunkSection section,
        int localX,
        int localY,
        int localZ,
        BlockState oldState,
        BlockState newState
    ) {}
}
