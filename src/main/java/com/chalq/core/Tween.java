package com.chalq.core;

import com.chalq.math.MathUtils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Tween {

    public enum Easing { LINEAR, EASE_IN, EASE_OUT, EASE_IN_OUT }
    private static final ArrayList<TweenTask> interpolations = new ArrayList<>();

    private static class TweenTask {

        private final Consumer<Float> setterFunc;
        private final float startVal, targetVal, startTime, duration;
        private final Easing easing;
        private boolean started = false;

        public TweenTask(Consumer<Float> setterFunc, float startVal, float targetVal, float startTime, float duration, Easing easing) {
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
                        x = easeIn(x);
                        break;
                    case EASE_OUT:
                        x = easeOut(x);
                        break;
                    case EASE_IN_OUT:
                        x = easeInOut(x);
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


    protected static void processInterpolations() {
        for (int i = interpolations.size() - 1; i >= 0; i--) {
            if (interpolations.get(i).update()) interpolations.remove(i);
        }
    }

    public static void interpolate(Consumer<Float> setterFunc, float startVal, float targetVal, float startTime, float duration, Easing easing) {
        interpolations.add(new TweenTask(setterFunc, startVal, targetVal, startTime, duration, easing));
    }

    public static float easeIn(float progress) {
        progress = MathUtils.clamp(progress, 0, 1);
        return progress * progress * progress;
    }

    public static float easeOut(float progress) {
        progress = MathUtils.clamp(progress, 0, 1);
        return (progress - 1) * (progress - 1) * (progress - 1) + 1;
    }

    public static float easeInOut(float progress) {
        progress = MathUtils.clamp(progress, 0, 1);
        return progress < 0.5 ?
                4 * progress * progress * progress :
                4 * (progress - 1) * (progress - 1) * (progress - 1) + 1;
    }

}
