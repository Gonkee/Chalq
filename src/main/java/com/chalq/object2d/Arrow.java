package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.object2d.path2d.Line;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

public class Arrow extends Object2D implements Traceable{

    private float width;
    private float pointX, pointY;
    private Color color = new Color(1, 1, 1, 1);

    private final Path2D path;
    private final float arcAngle;
    private boolean dirtyPath = true;

    // so that new ones don't have to be constantly made
    private final Vec2 tipV1 = new Vec2();
    private final Vec2 tipV2 = new Vec2();
    private final Vec2 tipV3 = new Vec2();

    public Arrow(float x, float y, float pointX, float pointY, float arcAngle, float width) {
        setPos(x, y);
        this.pointX = pointX;
        this.pointY = pointY;
        this.width = width;
        this.arcAngle = arcAngle;

        if (arcAngle == 0) {
            path = new Line(0, 0, pointX, pointY);
        } else {
            path = new ArcPath(0, 0, pointX, pointY, arcAngle);
        }

        path.setWidth(width);
        addChild(path);
    }

    @Override
    public void draw(long nvg) {

        if (dirtyPath) {
            if (arcAngle == 0) {
                ((Line) path).setTravel(pointX, pointY);
            } else {
                ((ArcPath) path).set(0, 0, pointX, pointY, arcAngle);
            }
            dirtyPath = false;
        }



        path.draw(nvg);

        Vec2 pathTracePos = path.getLocalTracePosition();
        float arrowTipRotation = arcAngle == 0 ? (float) Math.atan2(pointY, pointX) : ((ArcPath) path).tangentAngAtTrace();

        penBeginPath(nvg);
        penArrowTip(nvg, path.getX() + pathTracePos.x, path.getY() + pathTracePos.y, arrowTipRotation, width * 2 * path.getTraceProgress());
        penSetColor(color);
        penFillPath(nvg);
    }

    public void setColor(Color color) {
        this.color = color;
        path.color = color;
    }

    public float getPointX() {
        return pointX;
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointX(float pointX) {
        this.pointX = pointX;
        dirtyPath = true;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
        dirtyPath = true;
    }

    public void setWidth(float width) {
        this.width = width;
        path.setWidth(width);
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
