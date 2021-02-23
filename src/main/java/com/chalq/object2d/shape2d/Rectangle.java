package com.chalq.object2d.shape2d;

import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;

import static org.lwjgl.nanovg.NanoVG.*;

public class Rectangle extends Shape2D {

    public final Vec2 size = new Vec2();


    public Rectangle(float x, float y, float width, float height) {
        pos.x = x;
        pos.y = y;
        size.x = width;
        size.y = height;
        outline = new PolyPath(new float[] {
                pos.x, pos.y,
                pos.x + width, pos.y,
                pos.x + width, pos.y + height,
                pos.x, pos.y + height,
                pos.x, pos.y,
        } );
    }


    @Override
    protected void fillShape(long nvg) {
        nnvgBeginPath(nvg);
        nvgMoveTo(nvg, pos.x, pos.y);
        nvgLineTo(nvg, pos.x + size.x, pos.y);
        nvgLineTo(nvg, pos.x + size.x, pos.y + size.y);
        nvgLineTo(nvg, pos.x, pos.y + size.y);
        nvgLineTo(nvg, pos.x, pos.y);
        setColor(fillColor);
        fill(nvg);
    }
}
