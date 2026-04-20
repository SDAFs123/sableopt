package dev.sableopt.fix.sable;

import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "dev.ryanhcode.sable.ActiveSableCompanion", remap = false)
public class ActiveSableCompanionNullFixMixin {

    @Overwrite
    public Object getContaining(final Level level, final Position pos) {
        if (pos == null) {
            return null;
        }
        final int chunkX = Mth.floor(pos.x()) >> 4;
        final int chunkZ = Mth.floor(pos.z()) >> 4;
        try {
            var method = ((Object)this).getClass().getMethod("getContaining", Level.class, int.class, int.class);
            return method.invoke(this, level, chunkX, chunkZ);
        } catch (Exception e) {
            return null;
        }
    }

    @Overwrite
    public net.minecraft.world.phys.Vec3 projectOutOfSubLevel(final Level level, final Position pos) {
        if (pos == null) {
            return null;
        }
        try {
            var getContainingMethod = ((Object)this).getClass().getMethod("getContaining", Level.class, Position.class);
            Object subLevel = getContainingMethod.invoke(this, level, pos);
            if (subLevel == null) {
                if (pos instanceof final net.minecraft.world.phys.Vec3 vec) {
                    return vec;
                }
                return new net.minecraft.world.phys.Vec3(pos.x(), pos.y(), pos.z());
            }
            var projectMethod = ((Object)this).getClass().getMethod("projectOutOfSubLevel", Level.class, Position.class);
            return (net.minecraft.world.phys.Vec3) projectMethod.invoke(this, level, pos);
        } catch (Exception e) {
            if (pos instanceof final net.minecraft.world.phys.Vec3 vec) {
                return vec;
            }
            return new net.minecraft.world.phys.Vec3(pos.x(), pos.y(), pos.z());
        }
    }
}