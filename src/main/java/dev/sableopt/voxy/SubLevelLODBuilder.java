package dev.sableopt.voxy;

import dev.sableopt.SableOptimizations;
import dev.sableopt.voxy.SubLevelLODRenderer.LODMesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SubLevelLODBuilder {

    private int builtLODs = 0;

    public SubLevelLODBuilder() {
        SableOptimizations.LOGGER.debug("SubLevelLODBuilder initialized");
    }

    public LODMesh build(Object plot) {
        if (plot == null) {
            return new LODMesh(0, 0, null);
        }

        int estimatedVertices = estimateVertexCount(plot);
        int estimatedFaces = estimatedVertices / 3;
        
        Object meshData = extractMeshData(plot);
        
        LODMesh mesh = new LODMesh(estimatedVertices, estimatedFaces, meshData);
        builtLODs++;

        return mesh;
    }

    public LODMesh buildSimplified(Object plot, int simplificationLevel) {
        if (plot == null) {
            return new LODMesh(0, 0, null);
        }

        int baseVertices = estimateVertexCount(plot);
        int simplifiedVertices = applySimplification(baseVertices, simplificationLevel);
        int simplifiedFaces = simplifiedVertices / 3;

        Object meshData = extractSimplifiedMeshData(plot, simplificationLevel);

        LODMesh mesh = new LODMesh(simplifiedVertices, simplifiedFaces, meshData);
        builtLODs++;

        return mesh;
    }

    private int estimateVertexCount(Object plot) {
        return 256;
    }

    private int applySimplification(int baseVertices, int level) {
        float factor = 1.0f / (1 + level);
        return (int) (baseVertices * factor);
    }

    private Object extractMeshData(Object plot) {
        return plot;
    }

    private Object extractSimplifiedMeshData(Object plot, int level) {
        return plot;
    }

    public int getBuiltCount() {
        return builtLODs;
    }

    public void resetStats() {
        builtLODs = 0;
    }
}
