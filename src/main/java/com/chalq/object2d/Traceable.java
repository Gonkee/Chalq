package com.chalq.object2d;

import com.chalq.math.Scalar;
import com.chalq.math.Vec2;

public interface Traceable extends Drawable{

    void setTraceProgress(float progress);

    Scalar getTraceProgress();

    Vec2 getLocalTracePosition();

}
