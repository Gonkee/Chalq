package com.chalq.object2d.path2d;

import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;


public class ArcPath extends Path2D{

    private float radius, startAng, endAng;
    private boolean clockWise;

    public ArcPath(float centerX, float centerY, float radius, float startAng, float endAng, boolean clockWise) {
        pos.x = centerX;
        pos.y = centerY;
        this.startAng = startAng;
        this.endAng = endAng;
        this.radius = radius;
        this.clockWise = clockWise;
    }

    public ArcPath(float x1, float y1, float x2, float y2, float angleDegrees) {

        clockWise = angleDegrees > 0;
        angleDegrees %= 360;
        if (angleDegrees < 0) angleDegrees += 360;

        Vec2 to2ndPt = new Vec2(x2 - x1, y2 - y1);
        float halfChordLength = to2ndPt.len() / 2;
        float halfArcAngle = MathUtils.degreesToRadians * angleDegrees / 2;

        if (halfChordLength == 0) {
            new IllegalArgumentException("length cannot be 0").printStackTrace();
            return;
        }

        float chordDistToCenter = halfChordLength / (float) Math.tan(halfArcAngle);
        if (angleDegrees != 0) {
            radius = halfChordLength / (float) Math.sin(halfArcAngle);

            // must normalize then scale, setLength() only does positive values, messing up angles larger than 180 (cosine < 0)
            Vec2 chordMidpointToCenter = new Vec2(to2ndPt).rotateDeg(90).nor().scl(chordDistToCenter);
            Vec2 center = new Vec2( (x1 + x2) / 2, (y1 + y2) / 2 ).add( chordMidpointToCenter );
            pos.set(center);

            startAng = new Vec2(x1 - center.x, y1 - center.y).angle() * MathUtils.degreesToRadians;
            endAng   = new Vec2(x2 - center.x, y2 - center.y).angle() * MathUtils.degreesToRadians;
        } else {
            new IllegalArgumentException("Angle cannot be 0").printStackTrace();
        }
    }


    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
        penArc(nvg, pos.x, pos.y, radius, startAng, MathUtils.lerp(startAng, endAng, traceProgress.val), clockWise);
        penSetColor(color);
        penStrokePath(nvg, strokeWidth);
    }

    @Override
    public void update() {

    }

    @Override
    public Vec2 getLocalTracePosition() {
        return new Vec2(0, radius).rotateRad(MathUtils.lerp(startAng, endAng, traceProgress.val));//.add(pos);
    }

    public float tangentAngAtStart() {
        return startAng + (float) Math.PI / 2;
    }

    public float tangentAngAtEnd() {
        return endAng + (float) Math.PI / 2;
    }

    public float tangentAngAtTrace() {
        return MathUtils.lerp(startAng, endAng, traceProgress.val) + (float) Math.PI / 2;
    }

//    public Vec2 startRelativePos() {
//
//    }
}
