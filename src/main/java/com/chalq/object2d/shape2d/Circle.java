package com.chalq.object2d.shape2d;


import com.chalq.core.Cq;

import static org.lwjgl.nanovg.NanoVG.*;

public class Circle extends Shape2D {

    public float radius;

    @Override
    protected void update() {

    }

    @Override
    protected void fill() {
        nvgBeginPath(Cq.nvg);
        nvgMoveTo(Cq.nvg, pos.x, pos.y);
    }
}
