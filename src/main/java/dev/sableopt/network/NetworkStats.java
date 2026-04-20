package dev.sableopt.network;

import dev.sableopt.SableOptimizations;

public class NetworkStats {

    private long bytesSent = 0;
    private long bytesReceived = 0;
    private int packetsSent = 0;
    private int packetsReceived = 0;
    private long startTime = System.currentTimeMillis();

    private long lastLogTime = 0;
    private static final long LOG_INTERVAL = 5000;

    public void recordSent(int bytes) {
        bytesSent += bytes;
        packetsSent++;

        if (SableOptimizations.getConfig().logNetworkStats) {
            checkLog();
        }
    }

    public void recordReceived(int bytes) {
        bytesReceived += bytes;
        packetsReceived++;
    }

    public double getBytesPerSecond() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed == 0) return 0;
        return (bytesSent + bytesReceived) / (elapsed / 1000.0);
    }

    public double getSentKBps() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed == 0) return 0;
        return bytesSent / 1024.0 / (elapsed / 1000.0);
    }

    private void checkLog() {
        long now = System.currentTimeMillis();
        if (now - lastLogTime >= LOG_INTERVAL) {
            lastLogTime = now;
            SableOptimizations.LOGGER.info(
                "Network stats: sent {} KB/s, {} packets, received {} KB",
                String.format("%.2f", getSentKBps()),
                packetsSent,
                bytesReceived / 1024
            );
        }
    }

    public void reset() {
        bytesSent = 0;
        bytesReceived = 0;
        packetsSent = 0;
        packetsReceived = 0;
        startTime = System.currentTimeMillis();
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public int getPacketsSent() {
        return packetsSent;
    }
}
