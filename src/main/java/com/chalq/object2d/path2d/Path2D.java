package com.chalq.object2d.path2d;

import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Scalar;
import com.chalq.object2d.Traceable;
import com.chalq.util.Color;

public abstract class Path2D extends Object2D implements Traceable {

    public enum StrokeStyle {
        SOLID, DASHED, DOTTED
    }

    public StrokeStyle strokeStyle = StrokeStyle.SOLID;
    public float strokeWidth = 4;
    public Color color = new Color(1, 1, 1, 1);

    private float traceProgress = 1;



    @Override
    public void update() {
    }

    @Override
    public void setTraceProgress(float progress) {
        traceProgress = MathUtils.clamp(progress, 0, 1);
    }

    @Override
    public float getTraceProgress() {
        return traceProgress;
    }
}
