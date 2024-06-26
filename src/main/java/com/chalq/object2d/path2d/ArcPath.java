package com.chalq.object2d.path2d;

import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;


public class ArcPath extends Path2D{

    private float radius, startAng, endAng, traceAng;
    private int clockWise; // 1 = clockwise, -1 = counter clockwise
    private boolean dirtyBezier = true;

    // temp vector
    private final Vec2 tv1 = new Vec2();

    private final float[] bezier1 = new float[8];
    private final float[] bezier2 = new float[8];
    private final float[] bezier3 = new float[8];
    private final float[] bezier4 = new float[8];


    public ArcPath(float centerX, float centerY, float radius, float startAng, float endAng, boolean clockWise) {
        set(centerX, centerY, radius, startAng, endAng, clockWise);
    }

    public ArcPath(float x1, float y1, float x2, float y2, float angleDegrees) {
        set(x1, y1, x2, y2, angleDegrees);
    }

    public void set(float centerX, float centerY, float radius, float startAng, float endAng, boolean clockWise) {
        setPos(centerX, centerY);
        this.startAng = startAng * MathUtils.deg2rad;
        this.endAng = endAng * MathUtils.deg2rad;
        this.radius = radius;
        this.clockWise = clockWise ? 1 : -1;
        dirtyBezier = true;
    }

    public void set(float x1, float y1, float x2, float y2, float angleDegrees){

        clockWise = angleDegrees < 0 ? 1 : -1;
        angleDegrees %= 360;
        if (angleDegrees < 0) angleDegrees += 360;

        Vec2 to2ndPt = new Vec2(x2 - x1, y2 - y1);
        float halfChordLength = to2ndPt.len() / 2;
        float halfArcAngle = MathUtils.deg2rad * angleDegrees / 2;

        if (halfChordLength == 0) {
            new IllegalArgumentException("Length cannot be 0").printStackTrace();
            return;
        }

        float chordDistToCenter = halfChordLength / (float) Math.tan(halfArcAngle);
        if (angleDegrees != 0) {
            radius = halfChordLength / (float) Math.sin(halfArcAngle);

            // must normalize then scale, setLength() only does positive values, messing up angles larger than 180 (cosine < 0)
            Vec2 chordMidpointToCenter = new Vec2(to2ndPt).rotateDeg(90).nor().scl(chordDistToCenter);
            Vec2 center = new Vec2( (x1 + x2) / 2, (y1 + y2) / 2 ).add( chordMidpointToCenter );
            setPos(center.x, center.y);

            startAng = new Vec2(x1 - center.x, y1 - center.y).angle() * MathUtils.deg2rad;
            endAng   = new Vec2(x2 - center.x, y2 - center.y).angle() * MathUtils.deg2rad;
        } else {
            new IllegalArgumentException("Angle cannot be 0").printStackTrace();
        }
        dirtyBezier = true;
    }

    @Override
    public void setTraceProgress(float progress) {
        super.setTraceProgress(progress);
        dirtyBezier = true;
    }

    private void computeBeziers() {
        float angDiff = clockWise * (startAng - endAng);
        if (angDiff < 0) angDiff += 2 * Math.PI;
        angDiff *= getTraceProgress();
        traceAng = startAng + angDiff * -clockWise;

        float x1, y1;
        // rotate negative if clockwise, positive if counter clockwise
        tv1.set(radius, 0).rotateRad(startAng);
        x1 = tv1.x;
        y1 = tv1.y;
        tv1.rotateRad( angDiff / 4 * -clockWise );

        acuteArcBezier(x1, y1, tv1.x, tv1.y, bezier1);

        x1 = tv1.x;
        y1 = tv1.y;
        tv1.rotateRad( angDiff / 4 * -clockWise );

        acuteArcBezier(x1, y1, tv1.x, tv1.y, bezier2);

        x1 = tv1.x;
        y1 = tv1.y;
        tv1.rotateRad( angDiff / 4 * -clockWise );

        acuteArcBezier(x1, y1, tv1.x, tv1.y, bezier3);

        x1 = tv1.x;
        y1 = tv1.y;
        tv1.rotateRad( angDiff / 4 * -clockWise );
        
        acuteArcBezier(x1, y1, tv1.x, tv1.y, bezier4);
    }

    private void acuteArcBezier(float x1, float y1, float x2, float y2, float[] bezier) {
        float q1 = x1 * x1 + y1 * y1;
        float q2 = q1 + x1 * x2 + y1 * y2;
        float k2 = (4/3f) * ( (float) Math.sqrt(2 * q1 * q2) - q2) / (x1 * y2 - y1 * x2);
        bezier[0] = x1;
        bezier[1] = y1;
        bezier[2] = x1 - k2 * y1;
        bezier[3] = y1 + k2 * x1;
        bezier[4] = x2 + k2 * y2;
        bezier[5] = y2 - k2 * x2;
        bezier[6] = x2;
        bezier[7] = y2;
    }


    @Override
    public void draw(long nvg) {

        if (dirtyBezier) {
            computeBeziers();
            dirtyBezier = false;
        }

        penBeginPath(nvg);
        penMoveTo(nvg, bezier1[0], bezier1[1]);
        penBezierTo(nvg, bezier1[2], bezier1[3], bezier1[4], bezier1[5], bezier1[6], bezier1[7]);
        penBezierTo(nvg, bezier2[2], bezier2[3], bezier2[4], bezier2[5], bezier2[6], bezier2[7]);
        penBezierTo(nvg, bezier3[2], bezier3[3], bezier3[4], bezier3[5], bezier3[6], bezier3[7]);
        penBezierTo(nvg, bezier4[2], bezier4[3], bezier4[4], bezier4[5], bezier4[6], bezier4[7]);
        penSetColor(color);
        penStrokePath(nvg, width);
    }

    @Override
    public void update() {

    }

    @Override
    public Vec2 getLocalTracePosition() {
        return new Vec2(radius, 0).rotateRad(traceAng);
    }

    public Vec2 startRelativePos() {
        return new Vec2(radius, 0).rotateRad(startAng);
    }

    public Vec2 endRelativePos() {
        return new Vec2(radius, 0).rotateRad(endAng);
    }

    public float tangentAngAtStart() {
        return startAng + (float) Math.PI / 2;
    }

    public float tangentAngAtEnd() {
        return endAng + (float) Math.PI / 2;
    }

    public float tangentAngAtTrace() {
        return traceAng + (float) Math.PI / 2;
    }

}
