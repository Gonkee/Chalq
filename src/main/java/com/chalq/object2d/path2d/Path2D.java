package com.chalq.object2d.path2d;

import com.chalq.core.Object2D;
import com.chalq.math.Scalar;
import com.chalq.util.Color;

public abstract class Path2D extends Object2D {

    public enum StrokeStyle {
        SOLID, DASHED, DOTTED
    }

    public StrokeStyle strokeStyle = StrokeStyle.SOLID;
    public float strokeWidth = 4;
    public Color color = new Color(1, 1, 1, 1);

    public final Scalar traceProgress = new Scalar(0);

    public abstract void setProgress(float progress);


    @Override
    protected void update() {
        setProgress(traceProgress.val);
    }

}
