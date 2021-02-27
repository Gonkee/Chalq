package com.chalq.object2d;

import com.chalq.core.Cq;
import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.object2d.path2d.Line;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

import static org.lwjgl.nanovg.NanoVG.NVG_CW;

public class Arrow extends Object2D implements Traceable{

    public float width;
    public final Vec2 pointVector = new Vec2();
    public float arcAngle = 0;

    private final Scalar traceProgress = new Scalar(1);
    private final Path2D path;

    public Arrow(float x, float y, float pointX, float pointY, float arcAngle, float width) {
        this.pos.x = x;
        this.pos.y = y;
        this.pointVector.x = pointX;
        this.pointVector.y = pointY;
        this.width = width;
        if (arcAngle != 0) {
            path = new ArcPath(x, y, x + pointX, y + pointY, arcAngle);
        } else {
            path = new Line(x, y, x + pointX, y + pointY);
        }
    }

    @Override
    public void draw(long nvg) {

        // TODO: janky as hell betta clean up later

        path.draw(nvg);
//        float x1 = pos.x;
//        float y1 = pos.y;
//        float x2 = pos.x + pointVector.x;
//        float y2 = pos.y + pointVector.y;
//
//
//        if (arcAngle > 0) {
//            drawArc(nvg, pos.x, pos.y, pos.x + pointVector.x, pos.y + pointVector.y, arcAngle, width);
//        } else if (arcAngle < 0) {
//            drawArc(nvg, pos.x + pointVector.x, pos.y + pointVector.y, pos.x, pos.y, -arcAngle, width);
//        } else {
//            Cq.line(pos.x, pos.y, pos.x + pointVector.x, pos.y + pointVector.y, width);
//        }
//
//        float rotationDueToArc = 180 - 90 - (180 - arcAngle) / 2;
//
//        Vec2 tipDir = new Vec2(pointVector).nor().rotate(rotationDueToArc);
//
//        Vec2 tipV1 = new Vec2(tipDir).scl(width * 2).add(pos).add(pointVector);
//        Vec2 tipV2 = new Vec2(tipDir).scl(width * 2).rotate(-120).add(pos).add(pointVector);
//        Vec2 tipV3 = new Vec2(tipDir).scl(width * 2).rotate( 120).add(pos).add(pointVector);
//        Cq.fillPolygon(new float[] {tipV1.x, tipV1.y, tipV2.x, tipV2.y, tipV3.x, tipV3.y});
    }

    private void drawArc(long nvg, float x1, float y1, float x2, float y2, float angleDegrees, float strokeWidth) {

        angleDegrees %= 360;
        if (angleDegrees < 0) angleDegrees += 360;

        Vec2 to2ndPt = new Vec2(x2 - x1, y2 - y1);
        float chordLength = to2ndPt.len();
        if (chordLength == 0) return;

        penBeginPath(nvg);
        penSetColor(Color.WHITE);
//        penMoveTo(nvg, x1, y1);

        float chordToRadiusRatio = 2 * (float) Math.sin(MathUtils.degreesToRadians * angleDegrees / 2);
        if (chordToRadiusRatio != 0) {
            float radius = chordLength / chordToRadiusRatio;

            // must normalize then scale, setLength() only does positive values, messing up angles larger than 180 (cosine < 0)
            Vec2 chordMidpointToCenter = new Vec2(to2ndPt).rotate(90).nor().scl( radius * (float) Math.cos(MathUtils.degreesToRadians * angleDegrees / 2) );
            Vec2 center = new Vec2( (x1 + x2) / 2, (y1 + y2) / 2 ).add( chordMidpointToCenter );
            float ang1 = new Vec2(x1 - center.x, y1 - center.y).angle() * MathUtils.degreesToRadians;
            float ang2 = new Vec2(x2 - center.x, y2 - center.y).angle() * MathUtils.degreesToRadians;

            penArcClockWise(nvg, center.x, center.y, radius, ang1, MathUtils.lerp(ang1, ang2, traceProgress.val));
        } else {
            penLineTo(nvg, x2, y2);
        }
        penStrokePath(nvg, width);
    }

    @Override
    public void update() {

    }

    @Override
    public void setTraceProgress(float progress) {
        traceProgress.val = MathUtils.clamp(progress, 0, 1);
    }

    @Override
    public Scalar getTraceProgress() {
        return traceProgress;
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
