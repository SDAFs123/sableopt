package dev.sableopt.mixin.plugin;

import dev.sableopt.SableOptimizations;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SableOptMixinPlugin implements IMixinConfigPlugin {
    
    @Override
    public void onLoad(String mixinPackage) {
    }
    
    @Override
    public String getRefMapperConfig() {
        return null;
    }
    
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("Active") || mixinClassName.contains("sable") || mixinClassName.contains("SubLevel") || mixinClassName.contains("PoseStack")) {
            try {
                return net.neoforged.fml.ModList.get().getModContainerById("sable").isPresent();
            } catch (Exception e) {
                return true;
            }
        }
        if (mixinClassName.contains("voxy") || mixinClassName.contains("Voxy")) {
            try {
                return net.neoforged.fml.ModList.get().getModContainerById("sable").isPresent() 
                    && net.neoforged.fml.ModList.get().getModContainerById("voxy").isPresent();
            } catch (Exception e) {
                return true;
            }
        }
        return true;
    }
    
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }
    
    @Override
    public List<String> getMixins() {
        return null;
    }
    
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
    
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}