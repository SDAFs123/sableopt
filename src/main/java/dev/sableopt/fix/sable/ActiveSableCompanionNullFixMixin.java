package dev.sableopt.fix.sable;

import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.ryanhcode.sable.ActiveSableCompanion", remap = false)
public class ActiveSableCompanionNullFixMixin {

    @Inject(
        method = "getContaining(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Position;)Ldev/ryanhcode/sable/SubLevel;",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void sableopt$nullCheckGetContaining(Level level, Position pos, CallbackInfoReturnable<Object> cir) {
        if (pos == null) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
        method = "projectOutOfSubLevel(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/Position;)Lnet/minecraft/world/phys/Vec3;",
        at = @At("HEAD"),
        cancellable = true,
        require = 0
    )
    private void sableopt$nullCheckProjectOutOfSubLevel(Level level, Position pos, CallbackInfoReturnable<Object> cir) {
        if (pos == null) {
            cir.setReturnValue(null);
        }
    }
}