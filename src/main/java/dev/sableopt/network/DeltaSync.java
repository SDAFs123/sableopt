package dev.sableopt.network;

import org.joml.Vector3dc;
import org.joml.Vector3f;

public class DeltaSync {

    public enum ChangeType {
        NONE,
        POSITION_ONLY,
        ROTATION_ONLY,
        BOTH,
        VELOCITY_ONLY,
        FULL
    }

    private static final float POSITION_THRESHOLD = 0.015f / 16.0f;
    private static final float ROTATION_THRESHOLD = (float) Math.toRadians(0.015);
    private static final float VELOCITY_THRESHOLD = 0.001f;

    public static ChangeType detectChange(Vector3dc currentPos, Vector3dc lastPos,
                                          Object currentRot, Object lastRot,
                                          double[] linearVelocity) {
        boolean posChanged = !withinTolerance(currentPos, lastPos, POSITION_THRESHOLD);

        boolean rotChanged = false;
        if (currentRot != null && lastRot != null) {
            if (currentRot instanceof org.joml.Quaterniondc && lastRot instanceof org.joml.Quaterniondc) {
                rotChanged = !withinToleranceQuaternion((org.joml.Quaterniondc) currentRot, (org.joml.Quaterniondc) lastRot, ROTATION_THRESHOLD);
            }
        }

        boolean velChanged = false;
        if (linearVelocity != null) {
            Vector3f linearVel = new Vector3f(
                (float) linearVelocity[0],
                (float) linearVelocity[1],
                (float) linearVelocity[2]
            );
            velChanged = linearVel.length() > VELOCITY_THRESHOLD;
        }

        if (posChanged && rotChanged) {
            return ChangeType.BOTH;
        } else if (posChanged) {
            return ChangeType.POSITION_ONLY;
        } else if (rotChanged) {
            return ChangeType.ROTATION_ONLY;
        } else if (velChanged) {
            return ChangeType.VELOCITY_ONLY;
        }

        return ChangeType.NONE;
    }

    private static boolean withinTolerance(Vector3dc a, Vector3dc b, float threshold) {
        return Math.abs(a.x() - b.x()) < threshold
            && Math.abs(a.y() - b.y()) < threshold
            && Math.abs(a.z() - b.z()) < threshold;
    }

    private static boolean withinToleranceQuaternion(org.joml.Quaterniondc a, org.joml.Quaterniondc b, float threshold) {
        return Math.abs(a.x() - b.x()) < threshold
            && Math.abs(a.y() - b.y()) < threshold
            && Math.abs(a.z() - b.z()) < threshold
            && Math.abs(a.w() - b.w()) < threshold;
    }

    public static boolean shouldSend(ChangeType type) {
        return type != ChangeType.NONE;
    }
}
