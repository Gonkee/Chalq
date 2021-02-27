package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.object2d.path2d.Line;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

public class ArcArrow extends Object2D implements Traceable{

    public float width;
    public final Vec2 pointVector = new Vec2();
    public float arcAngle = 0;

    private final ArcPath path;

    public ArcArrow(float x, float y, float pointX, float pointY, float arcAngle, float width) {
        this.pos.x = x;
        this.pos.y = y;
        this.pointVector.x = pointX;
        this.pointVector.y = pointY;
        this.width = width;

        path = new ArcPath(0, 0, pointX, pointY, arcAngle);
        addChild(path);
    }

    @Override
    public void draw(long nvg) {

        penSetColor(new Color(1, 1, 1, 1));

        path.draw(nvg);
//
//        float rotationDueToArc = 180 - 90 - (180 - arcAngle) / 2;
        float arrowTipRotation = path.tangentAngAtTrace();
        Vec2 pathTracePos = path.getLocalTracePosition();
//        Vec2 tipDir = new Vec2(pointVector).nor().rotate(rotationDueToArc);

        Vec2 tipV1 = new Vec2(0, width * 2).rotateDeg(arrowTipRotation      ).add(pathTracePos);
        Vec2 tipV2 = new Vec2(0, width * 2).rotateDeg(arrowTipRotation - 120).add(pathTracePos);
        Vec2 tipV3 = new Vec2(0, width * 2).rotateDeg(arrowTipRotation + 120).add(pathTracePos);

        penBeginPath(nvg);
        penMoveTo(nvg, tipV1.x, tipV1.y);
        penLineTo(nvg, tipV2.x, tipV2.y);
        penLineTo(nvg, tipV3.x, tipV3.y);
        penLineTo(nvg, tipV1.x, tipV1.y);
        penFillPath(nvg);

        penSetColor(new Color(1, 0, 0, 1));
        penBeginPath(nvg);
        penCircle(nvg, 0, 0, 5);
        penCircle(nvg, pointVector.x, pointVector.y, 5);
        penFillPath(nvg);
//        Cq.fillPolygon(new float[] {tipV1.x, tipV1.y, tipV2.x, tipV2.y, tipV3.x, tipV3.y});
    }


    @Override
    public void update() {

    }

    @Override
    public void setTraceProgress(float progress) {
        path.traceProgress.val = MathUtils.clamp(progress, 0, 1);
    }

    @Override
    public Scalar getTraceProgress() {
        return path.traceProgress;
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
