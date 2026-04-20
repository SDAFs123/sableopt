package dev.sableopt.neoforge.mixin.physics;

import dev.sableopt.SableOptimizations;
import dev.sableopt.neoforge.physics.BlockUpdateBatcher;
import java.util.List;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem", remap = false)
public abstract class SubLevelPhysicsSystemMixin {

    @Unique
    private BlockUpdateBatcher sableopt$updateBatcher;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sableopt$initBatcher(ServerLevel level, CallbackInfo ci) {
        if (SableOptimizations.getConfig().enableBatchBlockUpdates) {
            this.sableopt$updateBatcher = new BlockUpdateBatcher();
            SableOptimizations.LOGGER.info("BlockUpdateBatcher initialized for level {}", level.dimension());
        }
    }

    @Inject(method = "handleBlockChange", at = @At("HEAD"), cancellable = true)
    private void sableopt$batchBlockChange(
        SectionPos sectionPos,
        LevelChunkSection section,
        int x, int y, int z,
        BlockState oldState,
        BlockState newState,
        CallbackInfo ci
    ) {
        if (!SableOptimizations.getConfig().enableBatchBlockUpdates || sableopt$updateBatcher == null) {
            return;
        }

        sableopt$updateBatcher.addUpdate(sectionPos, section, x, y, z, oldState, newState);
        ci.cancel();
    }

    @Inject(method = "tickPipelinePhysics", at = @At("HEAD"))
    private void sableopt$flushBlockUpdates(Object container, CallbackInfo ci) {
        if (sableopt$updateBatcher != null && !sableopt$updateBatcher.isEmpty()) {
            Object pipeline = this.getPipeline();
            List<BlockUpdateBatcher.PendingUpdate> updates = sableopt$updateBatcher.flush();

            long startTime = System.nanoTime();
            for (BlockUpdateBatcher.PendingUpdate update : updates) {
                invokeHandleBlockChange(pipeline, update);
            }
            long elapsed = System.nanoTime() - startTime;

            if (SableOptimizations.getConfig().logPhysicsStats && updates.size() > 10) {
                SableOptimizations.LOGGER.debug("Flushed {} block updates in {} us", updates.size(), elapsed / 1000);
            }
        }
    }

    @Unique
    private Object getPipeline() {
        try {
            var field = this.getClass().getDeclaredField("pipeline");
            field.setAccessible(true);
            return field.get(this);
        } catch (Exception e) {
            return null;
        }
    }

    @Unique
    private void invokeHandleBlockChange(Object pipeline, BlockUpdateBatcher.PendingUpdate update) {
        try {
            var method = pipeline.getClass().getMethod("handleBlockChange",
                SectionPos.class, LevelChunkSection.class,
                int.class, int.class, int.class,
                BlockState.class, BlockState.class);
            method.invoke(pipeline,
                update.sectionPos(), update.section(),
                update.localX(), update.localY(), update.localZ(),
                update.oldState(), update.newState());
        } catch (Exception e) {
            SableOptimizations.LOGGER.error("Failed to invoke handleBlockChange", e);
        }
    }
}