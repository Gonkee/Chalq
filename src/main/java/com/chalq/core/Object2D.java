package com.chalq.core;

import com.chalq.math.Mat3;
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

    private final Mat3 localTransform   = new Mat3();
    private final Mat3 globalTransform  = new Mat3();
    private boolean dirty = true;

    private float x, y;
    private float offsetX, offsetY;
    private float scaleX = 1, scaleY = 1;
    private float rotationRad = 0;

    // temp vectors so new Vec2s don't need to constantly be created
    private final Vec2 tv1 = new Vec2();
    private final Vec2 tv2 = new Vec2();
    private final Vec2 tv3 = new Vec2();

    public boolean visible = true;
    public boolean awake = true;

    @Override
    public float getRotation() {
        return rotationRad;
    }

    @Override
    public void setScale(float x, float y) {
        this.scaleX = x;
        this.scaleY = y;
        dirty = true;
    }

    @Override
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
        dirty = true;
    }

    @Override
    public void setOffset(float x, float y) {
        this.offsetX = x;
        this.offsetY = y;
        dirty = true;
    }

    @Override
    public void setRotation(float ang) {
        this.rotationRad = ang;
        dirty = true;
    }

    @Override
    public void setX(float x) {
        this.x = x;
        dirty = true;
    }

    @Override
    public void setY(float y) {
        this.y = y;
        dirty = true;
    }

    @Override
    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
        dirty = true;
    }

    @Override
    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        dirty = true;
    }

    @Override
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
        dirty = true;
    }

    @Override
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
        dirty = true;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getOffsetX() {
        return offsetX;
    }

    @Override
    public float getOffsetY() {
        return offsetY;
    }

    @Override
    public float getScaleX() {
        return scaleX;
    }

    @Override
    public float getScaleY() {
        return scaleY;
    }

    /*
        (p, q) = offset
        (x, y) = translation
        (u, v) = scale
        a = angle

        offset -> scale -> rotate -> translate (multiplying from right to left)
        | 1 , 0 , x |   | cos(a) , -sin(a) , 0 |   | u , 0 , 0 |   | 1 , 0 , p |
        | 0 , 1 , y | * | sin(a) ,  cos(a) , 0 | * | 0 , v , 0 | * | 0 , 1 , q |
        | 0 , 0 , 1 |   |   0    ,    0    , 1 |   | 0 , 0 , 1 |   | 0 , 0 , 1 |

        = | u * cos(a) , -v * sin(a) , p * u * cos(a) - q * v * sin(a) + x |
          | u * sin(a) ,  v * cos(a) , p * u * sin(a) + q * v * cos(a) + y |
          |     0      ,      0      ,                 1                   |
     */

    private void updateLocalTransform() {
        float sin = (float) Math.sin(rotationRad);
        float cos = (float) Math.cos(rotationRad);
        // element naming format: mXY (coordinates start at 0)
        localTransform.m20 = (offsetX * scaleX * cos) - (offsetY * scaleY * sin) + x;
        localTransform.m21 = (offsetX * scaleX * sin) + (offsetY * scaleY * cos) + y;
        localTransform.m00 = cos * scaleX;
        localTransform.m10 = -sin * scaleY;
        localTransform.m01 = sin * scaleX;
        localTransform.m11 = cos * scaleY;
        dirty = true;
    }

    private void updateGlobalTransform(Mat3 parentTransform) {
        Mat3.multiply(parentTransform, localTransform, globalTransform);
        dirty = false;
    }

    @Override
    public final void drawRecursive(long nvg, Mat3 parentTransform, boolean dirty) {
        if (visible) {
            dirty |= this.dirty;
            if (dirty) {
                updateLocalTransform();
                updateGlobalTransform(parentTransform);
            }
            draw(nvg);
            for (Drawable child : children) child.drawRecursive(nvg, globalTransform, dirty);
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


    protected void penBeginPath(long nvg) {
        nvgBeginPath(nvg);
    }
    
    protected void penMoveTo(long nvg, float x, float y) {
        tv1.set(x, y).transform(globalTransform);
        nvgMoveTo(nvg, tv1.x, tv1.y);
    }

    protected void penLineTo(long nvg, float x, float y) {
        tv1.set(x, y).transform(globalTransform);
        nvgLineTo(nvg, tv1.x, tv1.y);
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

    protected void penBezierTo(long nvg, float cx1, float cy1, float cx2, float cy2, float x, float y) {
        tv1.set(cx1, cy1).transform(globalTransform); // control point 1
        tv2.set(cx2, cy2).transform(globalTransform); // control point 2
        tv3.set(x, y).transform(globalTransform); // destination (control point 4)
        nvgBezierTo(nvg, tv1.x, tv1.y, tv2.x, tv2.y, tv3.x, tv3.y);
    }

//    protected void penText(long nvg, String text, float x, float y, float fontSize) {
//        tv1.set(x, y).transform(globalTransform);
//        float scaleX = (float) Math.sqrt(globalTransform.m00 * globalTransform.m00 + globalTransform.m01 * globalTransform.m01);
////        float scaleY = (float) Math.sqrt(globalTransform.m10 * globalTransform.m10 + globalTransform.m11 * globalTransform.m11);
//        Cq.textSettings(fontSize * scaleX, );
//    }

    protected void penCircle(long nvg, float x, float y, float radius) {
        tv1.set(x, y).transform(globalTransform);
//        radius = applyScale(radius);
        nvgCircle(nvg, tv1.x, tv1.y, radius);
    }

}
