package dev.sableopt.mixin.network;

import dev.sableopt.SableOptimizations;
import dev.sableopt.network.DistanceDecaySync;
import dev.sableopt.network.NetworkStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem")
public class SubLevelTrackingSystemMixin {

    @Unique
    private NetworkStats sableopt$stats = new NetworkStats();

    @Unique
    private DistanceDecaySync sableopt$distanceDecay = new DistanceDecaySync();

    @Unique
    private int sableopt$tickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void sableopt$onTickHead(CallbackInfo ci) {
        sableopt$tickCounter++;
    }

    @Inject(method = "sendMovementUpdates", at = @At("HEAD"))
    private void sableopt$beforeSendMovement(CallbackInfo ci) {
        if (!SableOptimizations.getConfig().enableDistanceDecay) {
            return;
        }
    }
}
