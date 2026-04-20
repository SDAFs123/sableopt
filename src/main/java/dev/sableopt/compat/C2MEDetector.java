package dev.sableopt.compat;

import dev.sableopt.OptimizationsConfig;
import dev.sableopt.SableOptimizations;

public class C2MEDetector {

    private static volatile boolean c2meLoaded = false;
    private static volatile boolean c2meChunkSystemEnabled = false;
    private static volatile boolean checked = false;

    public static void checkPresence() {
        if (checked) return;
        checked = true;

        OptimizationsConfig config = SableOptimizations.getConfig();
        if (!config.enableC2MECompat) {
            SableOptimizations.LOGGER.info("C2ME compatibility disabled in config");
            return;
        }

        try {
            Class.forName("com.ishland.c2me.base.ModuleEntryPoint");
            c2meLoaded = true;
            SableOptimizations.LOGGER.info("C2ME detected");

            try {
                Class<?> chunkSystemClass = Class.forName("com.ishland.c2me.rewrites.chunksystem.common.TheChunkSystem");
                c2meChunkSystemEnabled = true;
                SableOptimizations.LOGGER.warn("C2ME Chunk System detected - SubLevel physics may have reduced performance");
                SableOptimizations.LOGGER.info("C2ME chunk system replaces vanilla chunk handling, Sable's plot chunks will use fallback path");
            } catch (ClassNotFoundException e) {
                c2meChunkSystemEnabled = false;
                SableOptimizations.LOGGER.info("C2ME chunk system not present - standard compatibility");
            }

        } catch (ClassNotFoundException e) {
            c2meLoaded = false;
            SableOptimizations.LOGGER.info("C2ME not detected");
        }
    }

    public static boolean isC2MELoaded() {
        return c2meLoaded;
    }

    public static boolean isChunkSystemEnabled() {
        return c2meChunkSystemEnabled;
    }

    public static boolean shouldUseVanillaChunkPath() {
        return c2meChunkSystemEnabled;
    }
}
