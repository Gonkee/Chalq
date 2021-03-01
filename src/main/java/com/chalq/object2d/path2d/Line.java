package com.chalq.object2d.path2d;

import com.chalq.math.Vec2;


public class Line extends Path2D{

    public final Vec2 travel = new Vec2();


    public Line(float x, float y, float travelX, float travelY) {
        setPos(x, y);
        travel.x = travelX;
        travel.y = travelY;
    }



    @Override
    public void draw(long nvg) {

        penBeginPath(nvg);
        penMoveTo(nvg, getPos().x, getPos().y);

        switch (strokeStyle) {
            case SOLID:
                penLineTo(nvg, getPos().x + travel.x * traceProgress.val, getPos().y + travel.y * traceProgress.val);
                penSetColor(color);
                penStrokePath(nvg, strokeWidth);
                break;
            case DASHED:
                break;
            case DOTTED:
                break;
        }
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return new Vec2(getPos().x + travel.x * traceProgress.val, getPos().y + travel.y * traceProgress.val);
    }
}
