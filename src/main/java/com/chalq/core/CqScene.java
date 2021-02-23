package com.chalq.core;

import com.chalq.math.MathUtils;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;

import java.util.ArrayList;

public abstract class CqScene {

    private ArrayList<Object2D> drawables = new ArrayList<>();
    private ArrayList<Interpolation> interpolations = new ArrayList<>();

    private static abstract class Interpolation {
        public float startTime, duration;
        public boolean started = false;
        private Interpolation(float startTime, float duration) {
            this.startTime = startTime;
            this.duration = duration;
        }
        public abstract boolean update();
    }

    private static class ScalarInterpolation extends Interpolation {
        private final Scalar value;
        private final float end;

        private float start;

        private ScalarInterpolation(Scalar value, float to, float startTime, float duration) {
            super(startTime, duration);
            this.value = value;
            end = to;
        }
        public boolean update() {
            if (Cq.time >= startTime && !started) {
                started = true;
                start = value.val;
            }
            if (!started) return false;

            if (duration > 0) {
                // interpolating the value
                float progress = (Cq.time - startTime) / duration;
                boolean done = progress >= 1;
                if (done) progress = 1;
                value.val = MathUtils.lerp(start, end, progress);
                return done;
            } else {
                // immediately set value to target, then end the interpolation.
                value.val = end;
                return true;
            }
        }
    }

    private static class Vec2Interpolation extends Interpolation {
        private final Vec2 value;
        private final float endX, endY;
        private float startX, startY;
        private Vec2Interpolation(Vec2 value, Vec2 to, float startTime, float duration) {
            super(startTime, duration);
            this.value = value;
            endX = to.x;
            endY = to.y;
        }
        public boolean update() {
            if (Cq.time >= startTime && !started) {
                started = true;
                startX = value.x;
                startY = value.y;
            }
            if (!started) return false;

            if (duration > 0) {
                // interpolating the value
                float progress = (Cq.time - startTime) / duration;
                boolean done = progress >= 1;
                if (done) progress = 1;
                value.x = MathUtils.lerp(startX, endX, progress);
                value.y = MathUtils.lerp(startY, endY, progress);
                return done;
            } else {
                // immediately set value to target, then end the interpolation.
                value.x = endX;
                value.y = endY;
                return true;
            }
        }
    }


    public abstract void init();

    public abstract void update();

    public int addDrawable(Object2D drawable) {
        int id = drawables.size();
        drawables.add(drawable);
        return id;
    }

    public void removeDrawable(int id) {
        drawables.set(id, null);
    }

    protected void updateScene() {
        for (Object2D d : drawables) {
            if (d != null) {
                if (d.awake) d.update();
                d.draw(Cq.nvg);
            }
        }
        for (int i = interpolations.size() - 1; i >= 0; i--) {
            if (interpolations.get(i).update()) interpolations.remove(i);
        }

        update();
    }

    public void interpolate(Scalar value, float to, float startTime, float duration) {
        interpolations.add(new ScalarInterpolation(value, to, startTime, duration));
    }

    public void interpolate(Vec2 value, Vec2 to, float startTime, float duration) {
        interpolations.add(new Vec2Interpolation(value, to, startTime, duration));
    }
}
