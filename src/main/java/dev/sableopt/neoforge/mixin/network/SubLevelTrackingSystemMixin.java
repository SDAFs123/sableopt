package dev.sableopt.neoforge.mixin.network;

import dev.sableopt.SableOptimizations;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem")
public class SubLevelTrackingSystemMixin {

    @Shadow
    @Final
    private ServerLevel level;

    @Unique
    private int sableopt$filteredCount = 0;

    @Inject(method = "sendMovementUpdates", at = @At("HEAD"))
    private void sableopt$beforeMovementUpdates(Object container, CallbackInfo ci) {
        if (SableOptimizations.getConfig().enableDistanceDecay) {
            sableopt$filteredCount = 0;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void sableopt$endTick(Object container, CallbackInfo ci) {
        if (SableOptimizations.getConfig().enableDistanceDecay && SableOptimizations.getConfig().logNetworkStats) {
            if (sableopt$filteredCount > 0) {
                SableOptimizations.LOGGER.debug("Distance decay filtered {} network updates", sableopt$filteredCount);
            }
        }
    }
}
