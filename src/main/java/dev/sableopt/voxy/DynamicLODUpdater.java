package dev.sableopt.voxy;

import dev.sableopt.SableOptimizations;
import dev.sableopt.voxy.SubLevelLODRenderer.LODMesh;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DynamicLODUpdater {

    private int updates = 0;

    public DynamicLODUpdater() {
        SableOptimizations.LOGGER.debug("DynamicLODUpdater initialized");
    }

    public void update(LODMesh lod, Matrix4f newTransform) {
        if (lod == null) return;

        Matrix4f current = lod.getCurrentTransform();

        // 检查是否有显著变化
        if (!hasSignificantChange(current, newTransform)) {
            return;
        }

        // 更新变换矩阵（不重建网格）
        lod.setTransform(newTransform);
        updates++;
    }

    public void updatePosition(LODMesh lod, Vector3f newPosition) {
        if (lod == null) return;

        Matrix4f transform = lod.getCurrentTransform();
        transform.translation(newPosition);
        updates++;
    }

    public void updateRotation(LODMesh lod, Vector3f newRotation) {
        if (lod == null) return;

        Matrix4f transform = lod.getCurrentTransform();
        transform.rotateXYZ(newRotation.x, newRotation.y, newRotation.z);
        updates++;
    }

    public int getUpdateCount() {
        return updates;
    }

    public void resetStats() {
        updates = 0;
    }

    private boolean hasSignificantChange(Matrix4f current, Matrix4f newTransform) {
        float positionThreshold = 0.01f;
        float rotationThreshold = 0.01f;
        float scaleThreshold = 0.001f;

        Vector3f currentPos = new Vector3f();
        Vector3f newPos = new Vector3f();
        current.getTranslation(currentPos);
        newTransform.getTranslation(newPos);

        if (currentPos.distance(newPos) > positionThreshold) {
            return true;
        }

        AxisAngle4f currentRotAxis = new AxisAngle4f();
        AxisAngle4f newRotAxis = new AxisAngle4f();
        current.getRotation(currentRotAxis);
        newTransform.getRotation(newRotAxis);

        if (Math.abs(currentRotAxis.angle - newRotAxis.angle) > rotationThreshold) {
            return true;
        }

        Vector3f currentScale = new Vector3f();
        Vector3f newScale = new Vector3f();
        current.getScale(currentScale);
        newTransform.getScale(newScale);

        if (currentScale.distance(newScale) > scaleThreshold) {
            return true;
        }

        return false;
    }
}
