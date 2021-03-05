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
        penMoveTo(nvg, getX(), getY());

        switch (strokeStyle) {
            case SOLID:
                penLineTo(nvg, getX() + travel.x * getTraceProgress(), getY() + travel.y * getTraceProgress());
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
        return new Vec2(getX() + travel.x * getTraceProgress(), getY() + travel.y * getTraceProgress());
    }
}
