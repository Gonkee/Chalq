package com.chalq.core;

import com.chalq.object2d.Drawable;
import com.chalq.object2d.Traceable;

public class CqScene extends Object2D {


    @Override
    public final void draw(long nvg) {

    }

    @Override
    public void update() {

    }


    public void init() {

    }



    public void popUpObjectSlow(Drawable drawable, float targetScale, float delay) {
        addChild(drawable);
        drawable.setScale(0, 0);
        Tween.interpolate(drawable::setScaleX, 0, targetScale, Cq.time + delay, 0.8f, Tween.Easing.EASE_IN);
        Tween.interpolate(drawable::setScaleY, 0, targetScale, Cq.time + delay, 0.8f, Tween.Easing.EASE_IN);
    }

    public void popUpObjectFast(Drawable drawable, float targetScale, float delay) {
        addChild(drawable);
        drawable.setScale(0, 0);
        Tween.interpolate(drawable::setScaleX, 0, targetScale, Cq.time + delay, 0.2f, Tween.Easing.EASE_IN);
        Tween.interpolate(drawable::setScaleY, 0, targetScale, Cq.time + delay, 0.2f, Tween.Easing.EASE_IN);
    }

    public void traceObject(Traceable traceable, float delay) {
        addChild(traceable);
        traceable.setTraceProgress(0);
        Tween.interpolate(traceable::setTraceProgress, 0, 1, Cq.time + delay, 0.8f, Tween.Easing.EASE_IN_OUT);
    }

}
