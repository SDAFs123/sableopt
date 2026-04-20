package dev.sableopt.fix.sable;

import java.util.Optional;
import net.minecraft.core.Position;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.ryanhcode.sable.ActiveSableCompanion", remap = false)
public class ActiveSableCompanionNullFixMixin {

    @Inject(
        method = "getContaining",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sableopt$nullCheckGetContaining(Position pos, CallbackInfoReturnable<Optional<?>> cir) {
        if (pos == null) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(
        method = "projectOutOfSubLevel",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sableopt$nullCheckProjectOutOfSubLevel(Position pos, CallbackInfoReturnable<Position> cir) {
        if (pos == null) {
            cir.setReturnValue(null);
        }
    }
}