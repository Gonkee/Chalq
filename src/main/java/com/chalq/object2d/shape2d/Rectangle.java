package com.chalq.object2d.shape2d;

import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;

public class Rectangle extends Shape2D {

    public final Vec2 size = new Vec2();


    public Rectangle(float x, float y, float width, float height) {
        setPos(x, y);
        size.x = width;
        size.y = height;
        outline = new PolyPath(new float[] {
                0, 0,
                width, 0,
                width, height,
                0, height,
                0, 0,
        } );

        addChild(outline);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
        penMoveTo(nvg, 0, 0);
        penLineTo(nvg, size.x, 0);
        penLineTo(nvg, size.x, size.y);
        penLineTo(nvg, 0, size.y);
        penLineTo(nvg, 0, 0);
        penSetColor(fillColor);
        penFillPath(nvg);
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return outline.getLocalTracePosition();
    }
}
