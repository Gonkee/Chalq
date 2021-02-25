package com.chalq.object2d;

import com.chalq.math.Vec2;

public interface Drawable {

    void drawRecursive(long nvg);

    void updateRecursive();

    void draw(long nvg);

    void update();

    void addChild(Drawable drawable);

    Drawable getParent();

    void setParent(Drawable drawable);

    Vec2 applyTransform(float x, float y);

    Vec2 getScale();

    Vec2 getPos();
}
