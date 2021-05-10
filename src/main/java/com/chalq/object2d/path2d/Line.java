package com.chalq.object2d.path2d;

import com.chalq.math.Vec2;


public class Line extends Path2D{

    public float travelX, travelY;


    public Line(float x, float y, float travelX, float travelY) {
        setPos(x, y);
        this.travelX = travelX;
        this.travelY = travelY;
    }

    public void setTravel(float travelX, float travelY) {
        this.travelX = travelX;
        this.travelY = travelY;
    }

    @Override
    public void draw(long nvg) {

        penBeginPath(nvg);
        penMoveTo(nvg, getX(), getY());

        switch (strokeStyle) {
            case SOLID:
                penLineTo(nvg, getX() + travelX * getTraceProgress(), getY() + travelY * getTraceProgress());
                penSetColor(color);
                penStrokePath(nvg, width);
                break;
            case DASHED:
                break;
            case DOTTED:
                break;
        }
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return new Vec2(getX() + travelX * getTraceProgress(), getY() + travelY * getTraceProgress());
    }
}
