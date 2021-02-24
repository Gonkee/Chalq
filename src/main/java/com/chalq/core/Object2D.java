package com.chalq.core;

import com.chalq.math.Vec2;
import com.chalq.util.Color;
import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class Object2D {


    private static NVGColor penColor = NVGColor.create();

    private CqScene parentScene = null;
    public Object2D parent = null;
    public ArrayList<Object2D> children = new ArrayList<>();
    private int sceneID = -1;

    public boolean visible = true;
    public boolean awake = true;
    public final Vec2 pos = new Vec2();
    public final Vec2 scale = new Vec2(1, 1);
    public float rotationRad = 0;

    public final void drawRecursive(long nvg) {
        if (visible) {
            draw(nvg);
            for (Object2D child : children) child.drawRecursive(nvg);
        }
    }

    public final void updateRecursive() {
        if (awake) {
            update();
            for (Object2D child : children) child.updateRecursive();
        }
    }

    public abstract void draw(long nvg);

    protected abstract void update();

    public void addChild(Object2D object2D) {
        if (object2D.parent == null) {
            children.add(object2D);
            object2D.parent = this;
        }
    }

    private Vec2 localToGlobal(float x, float y) {

        if (parent != null) {
            Vec2 parentTransformed = parent.localToGlobal(x, y);
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
        x += parent == null ? pos.x : pos.x * parent.scale.x;
        y += parent == null ? pos.y : pos.y * parent.scale.y;

        return new Vec2(x, y);
    }
    
    protected void penBeginPath(long nvg) {
        nvgBeginPath(nvg);
    }
    
    protected void penMoveTo(long nvg, float x, float y) {
        Vec2 global = localToGlobal(x, y);
        nvgMoveTo(nvg, global.x, global.y);
    }

    protected void penLineTo(long nvg, float x, float y) {
        Vec2 global = localToGlobal(x, y);
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
