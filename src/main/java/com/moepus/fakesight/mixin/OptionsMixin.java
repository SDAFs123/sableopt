package com.moepus.fakesight.mixin;

import com.moepus.fakesight.FakeSightConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Redirect(method = "buildPlayerInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
    private Object fakesight$modifyPlayerInfoRenderDistance(OptionInstance<?> instance) {
        return Integer.valueOf(FakeSightConfig.CONFIG.requestDistance);
    }
}