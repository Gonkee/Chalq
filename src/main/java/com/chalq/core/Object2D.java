package com.chalq.core;

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
    public final Vec2 scale = new Vec2(1, 1);
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
    public Vec2 getScale() {
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
        x *= scale.x;
        y *= scale.y;

        // rotation
        float cos = (float)Math.cos(rotationRad);
        float sin = (float)Math.sin(rotationRad);
        x = x * cos - y * sin;
        y = x * sin + y * cos;

        // translation
        x += parent == null ? pos.x : pos.x * parent.getScale().x;
        y += parent == null ? pos.y : pos.y * parent.getScale().y;

        return new Vec2(x, y);
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

}
