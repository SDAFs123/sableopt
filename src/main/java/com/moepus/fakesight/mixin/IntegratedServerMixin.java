package com.moepus.fakesight.mixin;

import com.moepus.fakesight.FakeSightConfig;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @ModifyArg(method = "tickServer", at= @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 0), index = 1)
    private int fakesight$modifyRenderDistance(int b) {
        return FakeSightConfig.CONFIG.requestDistance;
    }
}