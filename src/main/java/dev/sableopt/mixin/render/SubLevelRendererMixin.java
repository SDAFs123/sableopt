package dev.sableopt.mixin.render;

import dev.sableopt.SableOptimizations;
import dev.sableopt.render.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.render.SubLevelRenderer")
public class SubLevelRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void sableopt$beforeRender(CallbackInfo ci) {
        RenderManager manager = RenderManager.getInstance();
        if (manager.isInitialized() && SableOptimizations.getConfig().enablePoseBatching) {
            manager.beforeRender();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void sableopt$afterRender(CallbackInfo ci) {
        RenderManager manager = RenderManager.getInstance();
        if (manager.isInitialized()) {
            manager.afterRender();
        }
    }
}
