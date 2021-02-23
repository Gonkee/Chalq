package com.chalq.object2d.shape2d;

import com.chalq.core.Cq;
import com.chalq.core.CqScene;
import com.chalq.core.Object2D;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

public abstract class Shape2D extends Object2D {

    public boolean stroke = true;
    public boolean fill = false;
    public Color fillColor = new Color(1, 1, 1, 1);

    public Path2D outline;


    @Override
    public void draw(long nvg) {
        if (fill) fillShape(nvg);
        if (stroke){
            outline.setProgress(outline.traceProgress.val);
            outline.draw(nvg);
        }
    }

    public void addAndTrace(CqScene scene, float x, float y) {
        addToScene(scene);
//        scale.set(0, 0);
        outline.traceProgress.val = 0;
//        fillColor
        scene.interpolate(outline.traceProgress, 1, Cq.time, 0.8f);
    }

    protected abstract void fillShape(long nvg);

    @Override
    protected void update() {

    }
}
