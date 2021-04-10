package com.chalq.core;

import com.chalq.math.LerpVal;
import com.chalq.math.MathUtils;
import com.chalq.object2d.Drawable;
import com.chalq.object2d.Traceable;

import java.util.ArrayList;

public class CqScene extends Object2D {

    private final ArrayList<Interpolation> interpolations = new ArrayList<>();

    @Override
    public final void draw(long nvg) {

    }

    @Override
    public void update() {

    }

    private static class Interpolation {

        public enum Type {
            POS_X, POS_Y, SCALE_X, SCALE_Y, OFFSET_X, OFFSET_Y, ROTATION, TRACE_PROGRESS, CUSTOM
        }

        private final Object subject;
        private final float startTime, duration, targetVal;
        private final Type type;

        private float startVal;
        private boolean started = false;

        public Interpolation(Object subject, Type type, float targetVal, float startTime, float duration) {
            this.subject = subject;
            this.type = type;
            this.targetVal = targetVal;
            this.startTime = startTime;
            this.duration = duration;
        }

        public boolean update() {
            if (Cq.time >= startTime && !started) {
                started = true;
                switch (type) {
                    case POS_X:
                        startVal = ((Drawable) subject).getX();
                        break;
                    case POS_Y:
                        startVal = ((Drawable) subject).getY();
                        break;
                    case SCALE_X:
                        startVal = ((Drawable) subject).getScaleX();
                        break;
                    case SCALE_Y:
                        startVal = ((Drawable) subject).getScaleY();
                        break;
                    case OFFSET_X:
                        startVal = ((Drawable) subject).getOffsetX();
                        break;
                    case OFFSET_Y:
                        startVal = ((Drawable) subject).getOffsetY();
                        break;
                    case ROTATION:
                        startVal = ((Drawable) subject).getRotation();
                        break;
                    case TRACE_PROGRESS:
                        startVal = ((Traceable) subject).getTraceProgress();
                        break;
                    case CUSTOM:
                        startVal = ((LerpVal) subject).val;
                        break;
                }
            }
            if (!started) return false;

            // interpolating the value
            boolean done;
            float current;
            if (duration > 0) {
                float progress = (Cq.time - startTime) / duration;
                done = progress >= 1;
                if (done) progress = 1;
                current = MathUtils.lerp(startVal, targetVal, progress);
            } else {
                done = true;
                current = targetVal;
            }
            switch (type) {
                case POS_X:
                    ((Drawable) subject).setPos(current,  ((Drawable) subject).getY());
                    break;
                case POS_Y:
                    ((Drawable) subject).setPos( ((Drawable) subject).getX(), current);
                    break;
                case SCALE_X:
                    ((Drawable) subject).setScale(current,  ((Drawable) subject).getScaleY());
                    break;
                case SCALE_Y:
                    ((Drawable) subject).setScale( ((Drawable) subject).getScaleX(), current);
                    break;
                case OFFSET_X:
                    ((Drawable) subject).setOffset(current,  ((Drawable) subject).getOffsetY());
                    break;
                case OFFSET_Y:
                    ((Drawable) subject).setOffset( ((Drawable) subject).getOffsetX(), current);
                    break;
                case ROTATION:
                    ((Drawable) subject).setRotation(current);
                    break;
                case TRACE_PROGRESS:
                    ((Traceable) subject).setTraceProgress(current);
                    break;
                case CUSTOM:
                    ((LerpVal) subject).val = current;
                    break;
            }
            return done;
        }
    }

//    private static abstract class Interpolation {
//        public float startTime, duration;
//        public boolean started = false;
//        private Interpolation(float startTime, float duration) {
//            this.startTime = startTime;
//            this.duration = duration;
//        }
//        public abstract boolean update();
//    }
//
//    private static class ScalarInterpolation extends Interpolation {
//        private final Scalar value;
//        private final float end;
//
//        private float start;
//
//        private ScalarInterpolation(Scalar value, float to, float startTime, float duration) {
//            super(startTime, duration);
//            this.value = value;
//            end = to;
//        }
//        public boolean update() {
//            if (Cq.time >= startTime && !started) {
//                started = true;
//                start = value.val;
//            }
//            if (!started) return false;
//
//            if (duration > 0) {
//                // interpolating the value
//                float progress = (Cq.time - startTime) / duration;
//                boolean done = progress >= 1;
//                if (done) progress = 1;
//                value.val = MathUtils.lerp(start, end, progress);
//                return done;
//            } else {
//                // immediately set value to target, then end the interpolation.
//                value.val = end;
//                return true;
//            }
//        }
//    }
//
//    private static class Vec2Interpolation extends Interpolation {
//        private final Vec2 value;
//        private final float endX, endY;
//        private float startX, startY;
//        private Vec2Interpolation(Vec2 value, Vec2 to, float startTime, float duration) {
//            super(startTime, duration);
//            this.value = value;
//            endX = to.x;
//            endY = to.y;
//        }
//        public boolean update() {
//            if (Cq.time >= startTime && !started) {
//                started = true;
//                startX = value.x;
//                startY = value.y;
//            }
//            if (!started) return false;
//
//            if (duration > 0) {
//                // interpolating the value
//                float progress = (Cq.time - startTime) / duration;
//                boolean done = progress >= 1;
//                if (done) progress = 1;
//                value.x = MathUtils.lerp(startX, endX, progress);
//                value.y = MathUtils.lerp(startY, endY, progress);
//                return done;
//            } else {
//                // immediately set value to target, then end the interpolation.
//                value.x = endX;
//                value.y = endY;
//                return true;
//            }
//        }
//    }


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

    public void lerpCustom(LerpVal value, float target, float startTime, float duration) {
        interpolations.add(new Interpolation(value, Interpolation.Type.CUSTOM, target, startTime, duration));
    }

    public void lerpPos(Drawable subject, float targetX, float targetY, float startTime, float duration) {
        interpolations.add(new Interpolation(subject, Interpolation.Type.POS_X, targetX, startTime, duration));
        interpolations.add(new Interpolation(subject, Interpolation.Type.POS_Y, targetY, startTime, duration));
    }

    public void lerpScale(Drawable subject, float targetX, float targetY, float startTime, float duration) {
        interpolations.add(new Interpolation(subject, Interpolation.Type.SCALE_X, targetX, startTime, duration));
        interpolations.add(new Interpolation(subject, Interpolation.Type.SCALE_Y, targetY, startTime, duration));
    }

    public void lerpOffset(Drawable subject, float targetX, float targetY, float startTime, float duration) {
        interpolations.add(new Interpolation(subject, Interpolation.Type.OFFSET_X, targetX, startTime, duration));
        interpolations.add(new Interpolation(subject, Interpolation.Type.OFFSET_Y, targetY, startTime, duration));
    }

    public void lerpRotation(Drawable subject, float target, float startTime, float duration) {
        interpolations.add(new Interpolation(subject, Interpolation.Type.ROTATION, target, startTime, duration));
    }

    public void lerpTraceProgress(Traceable subject, float target, float startTime, float duration) {
        interpolations.add(new Interpolation(subject, Interpolation.Type.TRACE_PROGRESS, target, startTime, duration));
    }

//    public void interpolate(Scalar value, float to, float startTime, float duration) {
//        interpolations.add(new ScalarInterpolation(value, to, startTime, duration));
//    }
//
//    public void interpolate(Vec2 value, Vec2 to, float startTime, float duration) {
//        interpolations.add(new Vec2Interpolation(value, to, startTime, duration));
//    }

    public void popUpObjectSlow(Object2D object2D, float targetScale, float delay) {
        addChild(object2D);
        object2D.setScale(0, 0);
        lerpScale(object2D, targetScale, targetScale, Cq.time + delay, 0.8f);
    }

    public void popUpObjectFast(Object2D object2D, float targetScale, float delay) {
        addChild(object2D);
        object2D.setScale(0, 0);
        lerpScale(object2D, targetScale, targetScale, Cq.time + delay, 0.2f);
    }

    public void traceObject(Traceable traceable, float delay) {
        addChild(traceable);
        traceable.setTraceProgress(0);
        lerpTraceProgress(traceable, 1, Cq.time + delay, 0.8f);
    }

}
