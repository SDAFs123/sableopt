package dev.sableopt.mixin.network;

import dev.sableopt.SableOptimizations;
import dev.sableopt.network.PrecisionCompressor;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.ryanhcode.sable.util.SableBufferUtils")
public class SableBufferUtilsMixin {

    @Inject(method = "write(Lio/netty/buffer/ByteBuf;Lorg/joml/Vector3dc;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private static void sableopt$onWriteVector3d(ByteBuf buf, Vector3dc vec, CallbackInfo ci) {
        if (!SableOptimizations.getConfig().enablePrecisionCompression) {
            return;
        }

        PrecisionCompressor.writeCompressedPosition(buf, vec);
        ci.cancel();
    }
}
