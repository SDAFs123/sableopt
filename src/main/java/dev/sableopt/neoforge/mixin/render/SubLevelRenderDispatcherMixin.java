package dev.sableopt.neoforge.mixin.render;

import dev.sableopt.SableOptimizations;
import dev.sableopt.neoforge.render.SubLevelPoseBatcher;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelRenderDispatcher")
public interface SubLevelRenderDispatcherMixin {

    @Inject(method = "preRenderChunks", at = @At("HEAD"))
    default void sableopt$preRenderBatch(Camera camera, CallbackInfo ci) {
        if (SableOptimizations.getConfig().enablePoseBatching) {
            SubLevelPoseBatcher.getInstance().beginFrame();
        }
    }

    @Inject(method = "addDebugInfo", at = @At("TAIL"))
    default void sableopt$addDebugInfo(Consumer<String> consumer, CallbackInfo ci) {
        if (SableOptimizations.getConfig().enablePoseBatching) {
            SubLevelPoseBatcher batcher = SubLevelPoseBatcher.getInstance();
            consumer.accept("SableOpt: Pose batch efficiency: " +
                String.format("%.1f%%", batcher.getBatchEfficiency() * 100));
        }
    }
}
