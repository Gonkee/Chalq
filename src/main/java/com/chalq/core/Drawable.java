package com.chalq.core;

import com.chalq.math.Vec2;

public abstract class Drawable {

    public boolean visible = true;
    public boolean awake = true;
    public Vec2 pos = new Vec2();

    protected abstract void draw();

    protected abstract void update();
}
