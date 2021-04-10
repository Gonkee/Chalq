package com.chalq.core;

import com.chalq.object2d.Drawable;
import com.chalq.object2d.Traceable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class CqScene extends Object2D {

    public enum Easing { LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT }
    private final ArrayList<Interpolation> interpolations = new ArrayList<>();

    @Override
    public final void draw(long nvg) {

    }

    @Override
    public void update() {

    }

    private static class Interpolation {


        private final Consumer<Float> setterFunc;
        private final float startVal, targetVal, startTime, duration;
        private final Easing easing;
        private boolean started = false;

        public Interpolation(Consumer<Float> setterFunc, float startVal, float targetVal, float startTime, float duration, Easing easing) {
            this.setterFunc = setterFunc;
            this.startVal = startVal;
            this.targetVal = targetVal;
            this.startTime = startTime;
            this.duration = duration;
            this.easing = easing;
        }

        public boolean update() {
            if (Cq.time >= startTime && !started) {
                started = true;
            }
            if (!started) return false;

            // interpolating the value
            boolean done;
            float current;
            if (duration > 0) {
                float x = (Cq.time - startTime) / duration;

                // all cubic easings
                switch (easing) {
                    case EASE_IN:
                        x = x * x * x;
                        break;
                    case EASE_OUT:
                        x = (x - 1) * (x - 1) * (x - 1) + 1;
                        break;
                    case EASE_IN_OUT:
                        x = x < 0.5 ? 4 * x * x * x : 4 * (x - 1) * (x - 1) * (x - 1) + 1;
                        break;
                }

                done = x >= 1;
                if (done) x = 1;
                current = startVal + (targetVal - startVal) * x;
            } else {
                done = true;
                current = targetVal;
            }
            setterFunc.accept(current);
            return done;
        }
    }

    public void init() {

    }

    protected void processInterpolations() {
        for (int i = interpolations.size() - 1; i >= 0; i--) {
            if (interpolations.get(i).update()) interpolations.remove(i);
        }
    }

    public void interpolate(Consumer<Float> setterFunc, float startVal, float targetVal, float startTime, float duration, Easing easing) {
        interpolations.add(new Interpolation(setterFunc, startVal, targetVal, startTime, duration, easing));
    }

    public void popUpObjectSlow(Drawable drawable, float targetScale, float delay) {
        addChild(drawable);
        drawable.setScale(0, 0);
        interpolate(drawable::setScaleX, 0, targetScale, Cq.time + delay, 0.8f, Easing.EASE_IN);
        interpolate(drawable::setScaleY, 0, targetScale, Cq.time + delay, 0.8f, Easing.EASE_IN);
    }

    public void popUpObjectFast(Drawable drawable, float targetScale, float delay) {
        addChild(drawable);
        drawable.setScale(0, 0);
        interpolate(drawable::setScaleX, 0, targetScale, Cq.time + delay, 0.2f, Easing.EASE_IN);
        interpolate(drawable::setScaleY, 0, targetScale, Cq.time + delay, 0.2f, Easing.EASE_IN);
    }

    public void traceObject(Traceable traceable, float delay) {
        addChild(traceable);
        traceable.setTraceProgress(0);
        interpolate(traceable::setTraceProgress, 0, 1, Cq.time + delay, 0.8f, Easing.EASE_IN_OUT);
    }

}
