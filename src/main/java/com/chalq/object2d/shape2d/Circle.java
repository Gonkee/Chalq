package com.chalq.object2d.shape2d;



import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.util.Color;

public class Circle extends Shape2D {


    private float radius;



    public Circle(float x, float y, float radius, Color color) {
        setPos(x, y);
        this.radius = radius;
        this.fillColor = color;

//        outline = new ArcPath(0, 0, radius, 0, (float) Math.PI * 2 + 0.1f, true);
//        addChild(outline);
    }



    @Override
    public void update() {

    }

    @Override
    public void draw(long nvg) {
        penSetColor(fillColor);
        penBeginPath(nvg);
        penCircle(nvg, 0, 0, radius * getScaleX());
        penFillPath(nvg);
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
