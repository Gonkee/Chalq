package com.chalq.object2d.path2d;

import com.chalq.core.Cq;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;


public class PolyPath extends Path2D{

    private float totalLength;

    private float[] vertices = new float[0]; // size = (number of segments + 1) * 2
    private float[] cumulativeLengths = new float[0]; // size = number of segments + 1

    private int completeSegments; // max size = number of segments
    private float incompleteSegmentLength;

    public PolyPath(float[] vertices) {
        setPath(vertices);
    }
    
    public void setPath(float[] vertices) {
        if (vertices.length < 4) throw new IllegalArgumentException("Path must contain at least 2 points.");
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Path points must have even number of coordinates.");
        this.vertices = vertices.clone();
        cumulativeLengths = new float[vertices.length / 2];

        totalLength = 0;
        float x1, y1, x2, y2, segmentLength;
        for (int i = 0; i < vertices.length; i += 2) {
            if (i == 0) {
                cumulativeLengths[0] = 0;
            } else {
                x1 = vertices[i - 2];
                y1 = vertices[i - 1];
                x2 = vertices[i    ];
                y2 = vertices[i + 1];
                segmentLength = (float) Math.sqrt( (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) );
                totalLength += segmentLength;
                cumulativeLengths[i / 2] = totalLength;
            }
        }
        setTraceProgress(getTraceProgress());
    }

    @Override
    public void setTraceProgress(float progress) {
        super.setTraceProgress(progress);
        float portionLength = totalLength * getTraceProgress();
        incompleteSegmentLength = 0;
        for (int i = 0; i < cumulativeLengths.length; i++) {
            // i >= 1, because cumulativeLengths[i] == 0
            if (portionLength >= cumulativeLengths[i]) {
                completeSegments = i;
            } else {
                incompleteSegmentLength = portionLength - cumulativeLengths[i - 1];
                break;
            }
        }
    }

    @Override
    public Vec2 getLocalTracePosition() {
        // there's 1 more cumulative length than maximum complete segments
        if (completeSegments < cumulativeLengths.length - 1) {
            float finalXdiff = vertices[completeSegments * 2 + 2] - vertices[completeSegments * 2];
            float finalYdiff = vertices[completeSegments * 2 + 3] - vertices[completeSegments * 2 + 1];
            float finalDist = (float) Math.sqrt(finalXdiff * finalXdiff + finalYdiff * finalYdiff);

            float finalX = vertices[completeSegments * 2] + finalXdiff / finalDist * incompleteSegmentLength;
            float finalY = vertices[completeSegments * 2 + 1] + finalYdiff / finalDist * incompleteSegmentLength;
            return new Vec2(finalX, finalY);
        } else {
            return new Vec2(vertices[vertices.length - 2], vertices[vertices.length - 1]);
        }
    }


    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
        penMoveTo(nvg, vertices[0], vertices[1]);
        for (int i = 2; i <= completeSegments * 2; i += 2) {
            penLineTo(nvg, vertices[i], vertices[i + 1]);
        }
        // there's 1 more cumulative length than maximum complete segments
        if (completeSegments < cumulativeLengths.length - 1) {
            float finalXdiff = vertices[completeSegments * 2 + 2] - vertices[completeSegments * 2];
            float finalYdiff = vertices[completeSegments * 2 + 3] - vertices[completeSegments * 2 + 1];
            float finalDist = (float) Math.sqrt(finalXdiff * finalXdiff + finalYdiff * finalYdiff);

            float finalX = vertices[completeSegments * 2] + finalXdiff / finalDist * incompleteSegmentLength;
            float finalY = vertices[completeSegments * 2 + 1] + finalYdiff / finalDist * incompleteSegmentLength;
            penLineTo(nvg, finalX, finalY);
        }
        Cq.stroke(4);
    }


}
