package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.object2d.path2d.Line;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

public class Arrow extends Object2D implements Traceable{

    public float width;
    public final Vec2 pointVector = new Vec2();

    private final Path2D path;
    private final boolean isArc;

    // so that new ones don't have to be constantly made
    private final Vec2 tipV1 = new Vec2();
    private final Vec2 tipV2 = new Vec2();
    private final Vec2 tipV3 = new Vec2();

    public Arrow(float x, float y, float pointX, float pointY, float arcAngle, float width) {
        setPos(x, y);
        this.pointVector.x = pointX;
        this.pointVector.y = pointY;
        this.width = width;

        if (arcAngle == 0) {
            path = new Line(0, 0, pointX, pointY);
            isArc = false;
        } else {
            path = new ArcPath(0, 0, pointX, pointY, arcAngle);
            isArc = true;
        }

        path.strokeWidth = width;
        addChild(path);
    }

    @Override
    public void draw(long nvg) {

        penSetColor(new Color(1, 1, 1, 1));

        path.draw(nvg);

        Vec2 pathTracePos = path.getLocalTracePosition();
        Vec2 pathPos = new Vec2(path.getX(), path.getY());
        float arrowTipRotation = isArc ? ((ArcPath) path).tangentAngAtTrace() : (float) Math.atan2(pointVector.y, pointVector.x);

        penBeginPath(nvg);
        penArrowTip(nvg, path.getX() + pathTracePos.x, path.getY() + pathTracePos.y, arrowTipRotation, width * 2 * path.getTraceProgress());
        penFillPath(nvg);

//        tipV1.set(width * 2 * path.getTraceProgress(), 0).rotateRad(arrowTipRotation).add(pathTracePos).add(pathPos);
//        tipV2.set(width * 2 * path.getTraceProgress(), 0).rotateRad(arrowTipRotation - 120 * MathUtils.deg2rad).add(pathTracePos).add(pathPos);
//        tipV3.set(width * 2 * path.getTraceProgress(), 0).rotateRad(arrowTipRotation + 120 * MathUtils.deg2rad).add(pathTracePos).add(pathPos);
//
//        penBeginPath(nvg);
//        penMoveTo(nvg, tipV1.x, tipV1.y);
//        penLineTo(nvg, tipV2.x, tipV2.y);
//        penLineTo(nvg, tipV3.x, tipV3.y);
//        penLineTo(nvg, tipV1.x, tipV1.y);
//        penFillPath(nvg);
    }


    @Override
    public void update() {

    }

    @Override
    public void setTraceProgress(float progress) {
        path.setTraceProgress(progress);
    }

    @Override
    public float getTraceProgress() {
        return path.getTraceProgress();
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
