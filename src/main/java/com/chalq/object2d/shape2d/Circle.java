package com.chalq.object2d.shape2d;



import com.chalq.object2d.path2d.ArcPath;

public class Circle extends Shape2D {

    public float radius;

    public Circle(float x, float y, float radius) {
        outline = new ArcPath(x, y, radius);
    }

    @Override
    protected void update() {

    }

    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
//        penCircle(nvg, pos.x, pos.y, radius);
        penFillPath(nvg);
    }
}