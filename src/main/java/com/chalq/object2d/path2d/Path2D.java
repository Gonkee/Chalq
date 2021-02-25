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

    public final Scalar traceProgress = new Scalar(0);



    @Override
    public void update() {
        setTraceProgress(traceProgress.val);
    }

    @Override
    public void setTraceProgress(float progress) {
        traceProgress.val = MathUtils.clamp(progress, 0, 1);
    }

    @Override
    public Scalar getTraceProgress() {
        return traceProgress;
    }
}
