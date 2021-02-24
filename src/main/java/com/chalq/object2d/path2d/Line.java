package com.chalq.object2d.path2d;

import com.chalq.math.Vec2;


public class Line extends Path2D{

    public final Vec2 travel = new Vec2();


    public Line(float x, float y, float travelX, float travelY) {
        pos.x = x;
        pos.y = y;
        travel.x = travelX;
        travel.y = travelY;
    }

    @Override
    public void setProgress(float progress) {

    }


    @Override
    public void draw(long nvg) {

        penBeginPath(nvg);
        penMoveTo(nvg, pos.x, pos.y);

        switch (strokeStyle) {
            case SOLID:
                penLineTo(nvg, pos.x + travel.x, pos.y + travel.y);
                penSetColor(color);
                penStrokePath(nvg, strokeWidth);
                break;
            case DASHED:
                break;
            case DOTTED:
                break;
        }
    }

}
