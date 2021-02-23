package com.chalq.object2d.shape2d;



import com.chalq.object2d.path2d.ArcPath;

import static org.lwjgl.nanovg.NanoVG.*;

public class Circle extends Shape2D {

    public float radius;

    public Circle(float x, float y, float radius) {
        outline = new ArcPath(x, y, radius);
    }

    @Override
    protected void update() {

    }

    @Override
    protected void fillShape(long nvg) {
        nvgBeginPath(nvg);
        nvgCircle(nvg, pos.x, pos.y, radius);
        fill(nvg);
    }
}
