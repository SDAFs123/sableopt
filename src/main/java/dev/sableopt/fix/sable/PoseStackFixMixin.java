package dev.sableopt.fix.sable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class PoseStackFixMixin {
    @Unique
    private static int sableopt$poseStackDepthBefore = 0;

    @Inject(method = "renderHitbox", at = @At("HEAD"), order = -1000)
    private static void sableopt$capturePoseStackDepthHead(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float partialTicks, float g, float h, float i, CallbackInfo ci) {
        sableopt$poseStackDepthBefore = ((PoseStackAccessor) poseStack).getPoseStack().size();
    }

    @Inject(method = "renderHitbox", at = @At("TAIL"), order = 2000)
    private static void sableopt$fixPoseStackBalanceTail(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float partialTicks, float g, float h, float i, CallbackInfo ci) {
        int currentDepth = ((PoseStackAccessor) poseStack).getPoseStack().size();
        int expectedDepth = sableopt$poseStackDepthBefore;
        
        while (currentDepth > expectedDepth) {
            poseStack.popPose();
            currentDepth--;
        }
        while (currentDepth < expectedDepth) {
            poseStack.pushPose();
            currentDepth++;
        }
    }
}