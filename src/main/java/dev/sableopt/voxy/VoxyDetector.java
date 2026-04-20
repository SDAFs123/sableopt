package dev.sableopt.voxy;

import dev.sableopt.SableOptimizations;

public class VoxyDetector {

    private static volatile boolean voxyLoaded = true;
    private static volatile boolean detected = true;

    public static void detect() {
        SableOptimizations.LOGGER.info("Voxy Sable Edition is running, compatibility layer active");
        detected = true;
        voxyLoaded = true;
    }

    public static boolean isVoxyLoaded() {
        return voxyLoaded;
    }

    public static void reset() {
        detected = false;
        voxyLoaded = false;
    }
}