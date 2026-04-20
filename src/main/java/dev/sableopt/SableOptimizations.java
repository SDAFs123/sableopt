package dev.sableopt;

import dev.sableopt.neoforge.config.SableOptConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("sableopt")
public class SableOptimizations {
    public static final Logger LOGGER = LoggerFactory.getLogger("sableopt");
    public static final String MOD_ID = "sableopt";
    
    public SableOptimizations(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing Sable Optimizations...");
        
        modContainer.registerConfig(ModConfig.Type.COMMON, SableOptConfig.CONFIG);
        
        LOGGER.info("Sable Optimizations initialized");
    }
    
    public static boolean isSableInstalled() {
        return net.neoforged.fml.ModList.get().getModContainerById("sable").isPresent();
    }
    
    public static OptimizationsConfig getConfig() {
        return SableOptConfig.getConfig();
    }
}