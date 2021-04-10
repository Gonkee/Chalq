package com.chalq.object2d;

import com.chalq.core.CqScene;
import com.chalq.math.Mat3;

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

    void setX(float x);
    void setY(float y);
    void setOffsetX(float offsetX);
    void setOffsetY(float offsetY);
    void setScaleX(float scaleX);
    void setScaleY(float scaleY);

}
