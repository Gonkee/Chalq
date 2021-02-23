package com.chalq.core;

import com.chalq.math.Vec2;
import com.chalq.util.Color;
import javafx.animation.Interpolator;
import org.lwjgl.nanovg.NVGColor;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class Object2D {


    private static NVGColor color = NVGColor.create();

    private CqScene parentScene = null;
    private int sceneID = -1;

    public boolean visible = true;
    public boolean awake = true;
    public final Vec2 pos = new Vec2();
    public final Vec2 scale = new Vec2(1, 1);
    public float rotationRad = 0;

    public abstract void draw(long nvg);

    protected abstract void update();

    protected static void setColor(float r, float g, float b, float a) {
        color.r(r);
        color.g(g);
        color.b(b);
        color.a(a);
    }

    protected static void setColor(Color color) {
        Object2D.color.r(color.r);
        Object2D.color.g(color.g);
        Object2D.color.b(color.b);
        Object2D.color.a(color.a);
    }

    protected static void stroke(long nvg, float strokeWidth) {
        nvgLineCap(nvg, NVG_ROUND);
        nvgStrokeColor(nvg, color);
        nvgStrokeWidth(nvg, strokeWidth);
        nvgStroke(nvg);
    }

    protected static void fill(long nvg) {
        nvgFillColor(nvg, color);
        nvgFill(nvg);
    }

    public boolean addToScene(CqScene scene) {
        if (parentScene == null) {
            sceneID = scene.addDrawable(this);
            parentScene = scene;
            return true;
        }
        return false;
    }

    public void popUpSlow(CqScene scene, float targetScale, float x, float y) {
        addToScene(scene);
        scale.set(0, 0);
        scene.interpolate(scale, new Vec2(targetScale, targetScale), Cq.time, 0.8f);
    }

    public void popUpFast(CqScene scene, float targetScale, float x, float y) {
        addToScene(scene);
        scale.set(0, 0);
        scene.interpolate(scale, new Vec2(targetScale, targetScale), Cq.time, 0.2f);
    }


    protected void drawLine(float x1, float y1, float x2, float y2, float strokeWidth) {

        // scaling
        x1 *= scale.x;
        y1 *= scale.y;
        x2 *= scale.x;
        y2 *= scale.y;

        // rotation
        float cos = (float)Math.cos(rotationRad);
        float sin = (float)Math.sin(rotationRad);
        x1 = x1 * cos - y1 * sin;
        y1 = x1 * sin + y1 * cos;
        x2 = x2 * cos - y2 * sin;
        y2 = x2 * sin + y2 * cos;

        // translation
        x1 += pos.x;
        y1 += pos.y;
        x2 += pos.x;
        y2 += pos.y;

        // scale stroke width by geometric mean of scale X and Y
//        strokeWidth *= Math.sqrt(scale.x * scale.y);

        Cq.line(x1, y1, x2, y2, strokeWidth);
    }

    private Vec2 localToGlobal(float x, float y) {
        // scaling
        x *= scale.x;
        y *= scale.y;

        // rotation
        float cos = (float)Math.cos(rotationRad);
        float sin = (float)Math.sin(rotationRad);
        x = x * cos - y * sin;
        y = x * sin + y * cos;

        // translation
        x += pos.x;
        y += pos.y;

        return new Vec2(x, y);
    }
    
    protected static void penBeginPath(long nvg) {
        nvgBeginPath(nvg);
    }
    
    protected static void penMoveTo(long nvg, float x, float y) {
        
    }

}
