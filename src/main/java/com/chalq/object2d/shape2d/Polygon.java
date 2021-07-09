package com.chalq.object2d.shape2d;

import com.chalq.core.Object2D;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;
import com.chalq.util.Color;

import java.util.Arrays;

public class Polygon extends Shape2D {

    private final PolyPath path;
    private int vertexCount;

    public Polygon(int vertexCount) {
        this.vertexCount = vertexCount;
        path = new PolyPath(new float[ (vertexCount + 1) * 2]);
        addChild(path);
    }

    public Polygon(float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("Vertices must contain at least 3 points.");
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Vertices must have even number of coordinates.");
        vertexCount = vertices.length / 2;

        float[] pathVertices = new float[vertices.length + 2];
        System.arraycopy(vertices, 0, pathVertices, 0, vertices.length);
        pathVertices[pathVertices.length - 2] = vertices[0];
        pathVertices[pathVertices.length - 1] = vertices[1];

        path = new PolyPath(pathVertices);
        addChild(path);
    }

    public void setVertexCoord(int coordID, float val) {
        path.setVertexCoord(coordID, val);
        if (coordID == 0) path.setVertexCoord(vertexCount * 2, val);
        if (coordID == 1) path.setVertexCoord(vertexCount * 2 + 1, val);
    }

    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
        penMoveTo(nvg, path.getVertexCoord(0), path.getVertexCoord(1));
        for (int coordID = 2; coordID < path.getVertexCount() * 2; coordID += 2) {
            penLineTo(nvg,
                    path.getVertexCoord(coordID),
                    path.getVertexCoord(coordID + 1));
        }
        penSetColor(fillColor);
        penFillPath(nvg);
    }

    @Override
    public void update() {

    }

    @Override
    public Vec2 getLocalTracePosition() {
        return path.getLocalTracePosition();
    }

    public void setStrokeColor (Color color) {
        path.color = color;
    }

    public int getVertexCount() {
        return vertexCount;
    }


    public void setTraceProgress (float progress) {
        path.setTraceProgress(progress);
    }
}
