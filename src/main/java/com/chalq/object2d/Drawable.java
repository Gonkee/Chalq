package com.chalq.object2d;

import com.chalq.math.Mat3;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;

public interface Drawable {

    void drawRecursive(long nvg, Mat3 parentTransform, boolean dirty);

    void updateRecursive();

    void draw(long nvg);

    void update();

    void addChild(Drawable drawable);

    Drawable getParent();

    void setParent(Drawable drawable);

//    Vec2 applyTransform(float x, float y);


    Vec2 getScale();
    void setScale(float x, float y);

    Vec2 getPos();
    void setPos(float x, float y);

    float getRotation();
    void setRotation(float ang);
}
