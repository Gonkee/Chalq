package com.chalq.object2d.shape2d;

import com.chalq.core.Object2D;
import com.chalq.math.Scalar;
import com.chalq.object2d.path2d.Path2D;

public abstract class Shape2D extends Object2D {

    public boolean stroke = true;
    public boolean fill = false;

    protected Path2D path;

    public final Scalar traceProgress = new Scalar(0);

    @Override
    protected void draw() {
        if (fill) fill();
        if (stroke) stroke();
    }

    protected void stroke() {
        path.setProgress(traceProgress.val);
        path.trace();
    }

    protected abstract void fill();
}
