package com.chalq.core;

import com.chalq.math.Scalar;
import com.chalq.math.Vec2;
import com.chalq.object2d.Drawable;
import com.chalq.util.Color;
import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class Object2D implements Drawable {


    private static NVGColor penColor = NVGColor.create();

    private CqScene parentScene = null;
    public Drawable parent = null;
    public ArrayList<Drawable> children = new ArrayList<>();
    private int sceneID = -1;

    public boolean visible = true;
    public boolean awake = true;
    public final Vec2 pos = new Vec2();
    public Scalar scale = new Scalar(1);
    public float rotationRad = 0;

    @Override
    public final void drawRecursive(long nvg) {
        if (visible) {
            draw(nvg);
            for (Drawable child : children) child.drawRecursive(nvg);
        }
    }

    @Override
    public final void updateRecursive() {
        if (awake) {
            update();
            for (Drawable child : children) child.updateRecursive();
        }
    }

    @Override
    public abstract void draw(long nvg);

    @Override
    public abstract void update();

    @Override
    public void addChild(Drawable drawable) {
        if (drawable.getParent() == null) {
            children.add(drawable);
            drawable.setParent(this);
        }
    }

    @Override
    public Drawable getParent() {
        return parent;
    }

    @Override
    public void setParent(Drawable drawable) {
        this.parent = drawable;
    }

    @Override
    public Vec2 getPos() {
        return pos;
    }

    @Override
    public Scalar getScale() {
        return scale;
    }

    @Override
    public Vec2 applyTransform(float x, float y) {
        if (parent != null) {
            Vec2 parentTransformed = parent.applyTransform(x, y);
            x = parentTransformed.x;
            y = parentTransformed.y;
        }

        // scaling
        x *= scale.val;
        y *= scale.val;

        // rotation
        float cos = (float)Math.cos(rotationRad);
        float sin = (float)Math.sin(rotationRad);
        x = x * cos - y * sin;
        y = x * sin + y * cos;

        // translation
        x += parent == null ? pos.x : pos.x * parent.getScale().val;
        y += parent == null ? pos.y : pos.y * parent.getScale().val;

        return new Vec2(x, y);
    }

    @Override
    public float applyScale(float dist) {
        if (parent != null) {
            dist = parent.applyScale(dist);
        }
        return dist * scale.val;
    }

    @Override
    public float applyRotation(float rotation) {
        if (parent != null) {
            rotation = parent.applyRotation(rotation);
        }
        return rotation + this.rotationRad;
    }

    protected void penBeginPath(long nvg) {
        nvgBeginPath(nvg);
    }
    
    protected void penMoveTo(long nvg, float x, float y) {
        Vec2 global = applyTransform(x, y);
        nvgMoveTo(nvg, global.x, global.y);
    }

    protected void penLineTo(long nvg, float x, float y) {
        Vec2 global = applyTransform(x, y);
        nvgLineTo(nvg, global.x, global.y);
    }

    protected void penStrokePath(long nvg, float strokeWidth) {
        nvgLineCap(nvg, NVG_ROUND);
        nvgStrokeColor(nvg, penColor);
        nvgStrokeWidth(nvg, strokeWidth);
        nvgStroke(nvg);
    }

    protected void penFillPath(long nvg) {
        nvgFillColor(nvg, penColor);
        nvgFill(nvg);
    }

    protected void penSetColor(float r, float g, float b, float a) {
        penColor.r(r);
        penColor.g(g);
        penColor.b(b);
        penColor.a(a);
    }

    protected void penSetColor(Color color) {
        Object2D.penColor.r(color.r);
        Object2D.penColor.g(color.g);
        Object2D.penColor.b(color.b);
        Object2D.penColor.a(color.a);
    }

    protected void penArcClockWise(long nvg, float x, float y, float radius, float startAng, float endAng) {
        Vec2 global = applyTransform(x, y);
        radius = applyScale(radius);
        startAng = applyRotation(startAng);
        endAng = applyRotation(endAng);
        nvgArc(nvg, x, y, radius, startAng, endAng, NVG_CW);
    }

}
