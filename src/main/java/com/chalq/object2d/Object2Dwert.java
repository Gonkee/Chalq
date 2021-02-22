package com.chalq.object2d;

import com.chalq.core.Cq;
import com.chalq.math.Vec2;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class Object2Dwert {

    public boolean visible = true;
    public boolean awake = true;
    public final Vec2 pos = new Vec2();
    public final Vec2 scale = new Vec2();
    public float rotationRad = 0;

    protected abstract void draw();

    protected abstract void update();


    protected void drawLine(float x1, float y1, float x2, float y2, float strokeWidth) {

        // scaling
        x1 *= scale.x;
        y1 *= scale.y;
        x2 *= scale.x;
        y2 *= scale.y;

        // rotation
        float cos = (float)Math.cos(rotationRad);
        float sin = (float)Math.sin(rotationRad);
        x1 = x1 * cos - y1 * sin;
        y1 = x1 * sin + y1 * cos;
        x2 = x2 * cos - y2 * sin;
        y2 = x2 * sin + y2 * cos;

        // translation
        x1 += pos.x;
        y1 += pos.y;
        x2 += pos.x;
        y2 += pos.y;

        // scale stroke width by geometric mean of scale X and Y
        strokeWidth *= Math.sqrt(scale.x * scale.y);

        Cq.line(x1, y1, x2, y2, strokeWidth);
    }
}
