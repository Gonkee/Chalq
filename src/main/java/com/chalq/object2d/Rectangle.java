package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.drawables.TracePath;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;

public class Rectangle extends Object2D {

    public final Vec2 size = new Vec2();
    private final TracePath tracePath;

    public final Scalar traceProgress = new Scalar(0);

    public Rectangle(float x, float y, float width, float height) {
        pos.x = x;
        pos.y = y;
        size.x = width;
        size.y = height;
        tracePath = new TracePath(new float[] {
                pos.x, pos.y,
                pos.x + width, pos.y,
                pos.x + width, pos.y + height,
                pos.x, pos.y + height,
                pos.x, pos.y,
        } );
    }

    @Override
    protected void draw() {
//        drawLine(0, 0, size.x, 0, 4);
//        drawLine(0, 0, 0, size.y, 4);
//        drawLine(size.x, 0, size.x, size.y, 4);
//        drawLine(0, 0 + size.y, size.x, size.y, 4);
        tracePath.setProgress(traceProgress.val);
        tracePath.draw();
    }

    @Override
    protected void update() {
    }
}
