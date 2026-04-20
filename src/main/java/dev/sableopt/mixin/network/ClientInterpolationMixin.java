package dev.sableopt.mixin.network;

import dev.sableopt.SableOptimizations;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.network.client.SubLevelSnapshotInterpolator")
public class ClientInterpolationMixin {

    @Inject(method = "receiveSnapshot", at = @At("TAIL"))
    private void sableopt$onReceiveSnapshot(Object subLevel, int tick, Object pose, Object mode, CallbackInfo ci) {
        if (!SableOptimizations.getConfig().enableClientPrediction) {
            return;
        }
    }
}
