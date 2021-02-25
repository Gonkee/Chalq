package com.chalq.object2d;

import com.chalq.math.Scalar;

public interface Traceable extends Drawable{

    void setTraceProgress(float progress);

    Scalar getTraceProgress();

}
