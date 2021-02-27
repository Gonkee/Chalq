package com.chalq.object2d.path2d;

import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.util.Color;

import static org.lwjgl.nanovg.NanoVG.NVG_CW;


public class ArcPath extends Path2D{

    private float radius, startAng, endAng;

    public ArcPath(float centerX, float centerY, float radius) {
        pos.x = centerX;
        pos.y = centerY;
        this.radius = radius;
    }

    public ArcPath(float x1, float y1, float x2, float y2, float angleDegrees) {

        angleDegrees %= 360;
        if (angleDegrees < 0) angleDegrees += 360;

        Vec2 to2ndPt = new Vec2(x2 - x1, y2 - y1);
        float chordLength = to2ndPt.len();
        if (chordLength == 0) {
            new IllegalArgumentException("length cannot be 0").printStackTrace();
            return;
        }

        float chordToRadiusRatio = 2 * (float) Math.sin(MathUtils.degreesToRadians * angleDegrees / 2);
        if (chordToRadiusRatio != 0) {
            radius = chordLength / chordToRadiusRatio;

            // must normalize then scale, setLength() only does positive values, messing up angles larger than 180 (cosine < 0)
            Vec2 chordMidpointToCenter = new Vec2(to2ndPt).rotate(90).nor().scl( radius * (float) Math.cos(MathUtils.degreesToRadians * angleDegrees / 2) );
            Vec2 center = new Vec2( (x1 + x2) / 2, (y1 + y2) / 2 ).add( chordMidpointToCenter );
            pos.set(center);
            startAng = new Vec2(x1 - center.x, y1 - center.y).angle() * MathUtils.degreesToRadians;
            endAng = new Vec2(x2 - center.x, y2 - center.y).angle() * MathUtils.degreesToRadians;

        } else {
            new IllegalArgumentException("Angle cannot be 0").printStackTrace();
            return;
        }
        System.out.println("arc path success, center = " + pos.toString() + ", radius = " + radius + ", ang1 = " + startAng + ", ang2 = " + endAng);
    }


    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
        penArcClockWise(nvg, pos.x, pos.y, radius, startAng, MathUtils.lerp(startAng, endAng, traceProgress.val));
        penSetColor(color);
        penStrokePath(nvg, strokeWidth);
    }

    @Override
    public void update() {

    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
