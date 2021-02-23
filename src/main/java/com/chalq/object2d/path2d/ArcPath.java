package com.chalq.object2d.path2d;

import com.chalq.math.MathUtils;

import static org.lwjgl.nanovg.NanoVG.*;

public class ArcPath extends Path2D{

    float radius;

    public ArcPath(float x, float y, float radius) {
        pos.x = x;
        pos.y = y;
        this.radius = radius;
    }

    @Override
    public void setProgress(float progress) {
        this.traceProgress.val = MathUtils.clamp(progress, 0, 1);
    }

    @Override
    public void draw(long nvg) {
        nvgBeginPath(nvg);
        nvgArc(nvg, pos.x, pos.y, radius, -(float)Math.PI / 2, -(float)Math.PI / 2 + traceProgress.val * (float)Math.PI * 2, NVG_CW);
        setColor(color);
        stroke(nvg, strokeWidth);
    }

    @Override
    protected void update() {

    }
}
