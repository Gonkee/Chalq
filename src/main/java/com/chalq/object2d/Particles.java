package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.util.Color;

import java.util.ArrayList;

public class Particles extends Object2D {

    // arrays go { x1, y1, x2, y2 ... xn, yn }
    private final ArrayList<float[]> particles = new ArrayList<>();
    private final int trailSegments, framesPerSegment;
    private final float particleSize;
    private final float trailWidth;
    private final Color color;

    int yee = 0;

    public Particles(float particleSize, int trailSegments, float trailWidth, int framesPerSegment, Color color) {
        trailSegments = MathUtils.clamp(trailSegments, 0, 500);
        this.trailSegments = trailSegments;
        this.framesPerSegment = framesPerSegment;
        this.particleSize = particleSize;
        this.trailWidth = trailWidth;
        this.color = color;
    }

    public int addParticle(float x, float y) {
        float[] p = new float[2 + trailSegments * 2];
        for (int i = 0; i < p.length; i += 2) {
            p[i    ] = x;
            p[i + 1] = y;
        }
        particles.add(p);
        return particles.size() - 1;
    }

    public void updateParticle(int id, float x, float y) {
        float[] p = particles.get(id);
        if ( yee == 0 ) {
            for (int i = p.length - 2; i >= 2; i -= 2) {
                p[i    ] = p[i - 2];
                p[i + 1] = p[i - 1];
            }
        }
        p[0] = x; // this must go afterwards, or else trail stutters every new segment
        p[1] = y;
    }

    @Override
    public void draw(long nvg) {

        penSetColor(color);

        if (particleSize > 0) {
            penBeginPath(nvg);
            for (float[] p : particles) {

                penCircle(nvg, p[0], p[1], particleSize);
            }
            penFillPath(nvg);
        }

        for (float[] p : particles) {

            // the last segment will shorten as the first segment lengthens, to prevent choppiness of updating segments
            float lastSegmentPortion =
                    1 - yee / (float) framesPerSegment;

            penBeginPath(nvg);
            penMoveTo(nvg, p[0], p[1]);
            for (int i = 2; i < p.length - 2; i += 2)
                penLineTo(nvg, p[i], p[i + 1]);
            penLineTo(nvg,
                    MathUtils.lerp(p[p.length - 4], p[p.length - 2], lastSegmentPortion),  // final point X position
                    MathUtils.lerp(p[p.length - 3], p[p.length - 1], lastSegmentPortion)); // final point Y position
            penStrokePath(nvg, trailWidth);


//            float width = trailWidth;
//            for (int i = 2; i < p.length - 2; i += 2) {
//                penBeginPath(nvg);
//                penMoveTo(nvg, p[i - 2], p[i - 1]);
//                penLineTo(nvg, p[i    ], p[i + 1]);
//                penStrokePath(nvg, width);
//                width *= 0.95;
//            }
//            penBeginPath(nvg);
//            penMoveTo(nvg, p[p.length - 4], p[p.length - 3]);
//            penLineTo(nvg,
//                    MathUtils.lerp(p[p.length - 4], p[p.length - 2], lastSegmentPortion),  // final point X position
//                    MathUtils.lerp(p[p.length - 3], p[p.length - 1], lastSegmentPortion)); // final point Y position
//            penStrokePath(nvg, width);
        }
        yee = (yee + 1) % framesPerSegment;
    }

    @Override
    public void update() {

    }

    public int getSize() {
        return particles.size();
    }

    public float getX(int id) {
        return particles.get(id)[0];
    }

    public float getY(int id) {
        return particles.get(id)[1];
    }
}
