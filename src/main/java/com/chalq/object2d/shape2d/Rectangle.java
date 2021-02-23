package com.chalq.object2d.shape2d;

import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;

public class Rectangle extends Shape2D {

    public final Vec2 size = new Vec2();
    private final PolyPath polyPath;


    public Rectangle(float x, float y, float width, float height) {
        pos.x = x;
        pos.y = y;
        size.x = width;
        size.y = height;
        polyPath = new PolyPath(new float[] {
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
        polyPath.setProgress(traceProgress.val);
        polyPath.draw();
    }

    @Override
    protected void update() {
    }
}
