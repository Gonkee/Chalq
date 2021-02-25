package com.chalq.core;

import com.chalq.math.MathUtils;
import com.chalq.math.Scalar;
import com.chalq.math.Vec2;
import com.chalq.object2d.Traceable;
import com.chalq.object2d.shape2d.Shape2D;

import java.util.ArrayList;

public class CqScene extends Object2D {

    private ArrayList<Interpolation> interpolations = new ArrayList<>();

    @Override
    public final void draw(long nvg) {

    }

    @Override
    public void update() {

    }

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


    public void init() {

    }



//    protected void updateScene() {
//        for (Object2D d : drawables) {
//            if (d != null) {
//                if (d.awake) d.updateRecursive();
//                d.drawRecursive(Cq.nvg);
//            }
//        }
//
//        update();
//    }
    protected void processInterpolations() {
        for (int i = interpolations.size() - 1; i >= 0; i--) {
            if (interpolations.get(i).update()) interpolations.remove(i);
        }
    }

    public void interpolate(Scalar value, float to, float startTime, float duration) {
        interpolations.add(new ScalarInterpolation(value, to, startTime, duration));
    }

    public void interpolate(Vec2 value, Vec2 to, float startTime, float duration) {
        interpolations.add(new Vec2Interpolation(value, to, startTime, duration));
    }

    public void popUpObjectSlow(Object2D object2D, float targetScale, float x, float y) {
        addChild(object2D);
        object2D.pos.x = x;
        object2D.pos.y = y;
        object2D.scale.set(0, 0);
        interpolate(object2D.scale, new Vec2(targetScale, targetScale), Cq.time, 0.8f);
    }

    public void popUpObjectFast(Object2D object2D, float targetScale, float x, float y) {
        addChild(object2D);
        object2D.pos.x = x;
        object2D.pos.y = y;
        object2D.scale.set(0, 0);
        interpolate(object2D.scale, new Vec2(targetScale, targetScale), Cq.time, 0.2f);
    }

    public void traceObject(Traceable traceable, float x, float y) {
        addChild(traceable);
        traceable.getPos().x = x;
        traceable.getPos().y = y;
        traceable.setTraceProgress(0);
        interpolate(traceable.getTraceProgress(), 1, Cq.time, 0.8f);
    }

}
