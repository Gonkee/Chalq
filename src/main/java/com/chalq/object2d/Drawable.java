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


    float getX();
    float getY();
    float getOffsetX();
    float getOffsetY();
    float getScaleX();
    float getScaleY();
    float getRotation();

    void setPos(float x, float y);
    void setOffset(float x, float y);
    void setScale(float x, float y);
    void setRotation(float ang);
}
