package dev.sableopt.mixin.voxy;

import dev.sableopt.SableOptimizations;
import dev.sableopt.voxy.VoxyDetector;
import dev.sableopt.voxy.VoxyManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me.cortex.voxy.client.core.VoxyRenderSystem", remap = false)
public class VoxyRenderSystemMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void sableopt$onRender(CallbackInfo ci) {
        if (!SableOptimizations.getConfig().enableSublevelLod) {
            return;
        }

        if (!VoxyDetector.isVoxyLoaded()) {
            return;
        }

        if (!SableOptimizations.isSableInstalled()) {
            return;
        }

        VoxyManager manager = VoxyManager.getInstance();
        if (manager.shouldUseSubLevelLOD()) {
            manager.renderSubLevelLOD(0, null, null);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void sableopt$afterRender(CallbackInfo ci) {
        if (!SableOptimizations.getConfig().enableSublevelLod) {
            return;
        }

        VoxyManager manager = VoxyManager.getInstance();
        if (manager.isInitialized()) {
            manager.logStats();
        }
    }
}
