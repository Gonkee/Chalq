package com.chalq.object2d.shape2d;

import com.chalq.core.Cq;
import com.chalq.core.CqScene;
import com.chalq.core.Object2D;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;
import com.chalq.object2d.Traceable;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

public abstract class Shape2D extends Object2D implements Traceable {

    public Color fillColor = new Color(1, 1, 1, 1);

    public Path2D outline;

    @Override
    public void setTraceProgress(float progress) {
        outline.setTraceProgress(progress);
    }

    @Override
    public Scalar getTraceProgress() {
        return outline.getTraceProgress();
    }
}
