package com.chalq.core;

import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.util.Color;
import org.lwjgl.nanovg.NVGColor;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;

public class Cq {

    private Cq() {}

    public static float time = 0;
    public static float delta = 0;
    public static float frameTime = 0;
    private static boolean initialized = false;
    private static long nvg;
    private static NVGColor color;

    protected static int width;
    protected static int height;
    public static int getFrameWidth() { return width; }
    public static int getFrameHeight() { return height; }

    private static final ArrayList<Object> preventGarbageCollect = new ArrayList<>();

    public enum TextAlignH { LEFT, CENTER, RIGHT }
    public enum TextAlignV { TOP, CENTER, BOTTOM }

    private static int alignH = NVG_ALIGN_CENTER;
    private static int alignV = NVG_ALIGN_MIDDLE;

    protected static void init(long nvg, Color bgColor) {
        Cq.nvg = nvg;
        initialized = true;

        glClearColor(bgColor.r, bgColor.g, bgColor.b, 1);
        color = NVGColor.create();

        ByteBuffer fontBuffer = IOUtils.ioResourceToByteBuffer("bahnschrift.ttf", 40 * 1024);
        preventGarbageCollect.add(fontBuffer);
        int font = nvgCreateFontMem(nvg, "bahnschrift", fontBuffer, 0);

        // initial settings
        setColor(1, 1, 1, 1);
        textSettings(40, TextAlignH.CENTER, TextAlignV.CENTER);
    }

    private static void checkInit() {
        if (!initialized) throw new IllegalStateException("Chalq has not been initialized! Make sure CqWindow is fully initialized");
    }

    private static void fill() {
        nvgFillColor(nvg, color);
        nvgFill(nvg);
    }

    private static void stroke(float strokeWidth) {
        nvgLineCap(nvg, NVG_ROUND);
        nvgStrokeColor(nvg, color);
        nvgStrokeWidth(nvg, strokeWidth);
        nvgStroke(nvg);
    }

    public static void textSettings(float fontSize, TextAlignH alignH, TextAlignV alignV) {
        checkInit();
        nvgFontSize(nvg, fontSize);
        switch (alignH) {
            case LEFT:
                Cq.alignH = NVG_ALIGN_LEFT;
                break;
            case CENTER:
                Cq.alignH = NVG_ALIGN_CENTER;
                break;
            case RIGHT:
                Cq.alignH = NVG_ALIGN_RIGHT;
                break;
        }
        switch (alignV) {
            case TOP:
                Cq.alignV = NVG_ALIGN_TOP;
                break;
            case CENTER:
                Cq.alignV = NVG_ALIGN_MIDDLE;
                break;
            case BOTTOM:
                Cq.alignV = NVG_ALIGN_BOTTOM;
                break;
        }
        nvgTextAlign(nvg, Cq.alignH | Cq.alignV);
    }

    public static void setColor(float r, float g, float b, float a) {
        checkInit();
        color.r(r);
        color.g(g);
        color.b(b);
        color.a(a);
    }

    public static void setColor(Color color) {
        checkInit();
        Cq.color.r(color.r);
        Cq.color.g(color.g);
        Cq.color.b(color.b);
        Cq.color.a(color.a);
    }

    public static void setBackgroundColor(float r, float g, float b) {
        checkInit();
        glClearColor(r, g, b, 1);
    }

    public static void setBackgroundColor(Color bgColor) {
        checkInit();
        glClearColor(bgColor.r, bgColor.g, bgColor.b, 1);
    }

    public static void clearFrame() {
        checkInit();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public static void fillCircle(float x, float y, float radius) {
        checkInit();
        nvgBeginPath(nvg);
        nvgCircle(nvg, x, y, radius);
        fill();
    }

    public static void strokeCircle(float x, float y, float radius, float strokeWidth) {
        checkInit();
        nvgBeginPath(nvg);
        nvgCircle(nvg, x, y, radius);
        stroke(strokeWidth);
    }

    public static void line(float x1, float y1, float x2, float y2, float strokeWidth) {
        checkInit();
        nvgBeginPath(nvg);
        nvgMoveTo(nvg, x1, y1);
        nvgLineTo(nvg, x2, y2);
        stroke(strokeWidth);
    }

    public static void arcClockwise(float x1, float y1, float x2, float y2, float angleDegrees, float strokeWidth) {

        angleDegrees %= 360;
        if (angleDegrees < 0) angleDegrees += 360;

        Vec2 to2ndPt = new Vec2(x2 - x1, y2 - y1);
        float chordLength = to2ndPt.len();
        if (chordLength == 0) return;

        float chordToRadiusRatio = 2 * (float) Math.sin(MathUtils.degreesToRadians * angleDegrees / 2);
        if (chordToRadiusRatio != 0) {
            float radius = chordLength / chordToRadiusRatio;

            // must normalize then scale, setLength() only does positive values, messing up angles larger than 180 (cosine < 0)
            Vec2 chordMidpointToCenter = new Vec2(to2ndPt).rotate(90).nor().scl( radius * (float) Math.cos(MathUtils.degreesToRadians * angleDegrees / 2) );
            Vec2 center = new Vec2( (x1 + x2) / 2, (y1 + y2) / 2 ).add( chordMidpointToCenter );
            float ang1 = new Vec2(x1 - center.x, y1 - center.y).angle() * MathUtils.degreesToRadians;
            float ang2 = new Vec2(x2 - center.x, y2 - center.y).angle() * MathUtils.degreesToRadians;

            checkInit();
            nvgBeginPath(nvg);
            nvgMoveTo(nvg, x1, y1);
            nvgArc(nvg, center.x, center.y, radius, ang1, ang2, NVG_CW);
            stroke(strokeWidth);
        } else {
            line(x1, y1, x2, y2, strokeWidth);
        }
    }

    public static void fillRect(float x, float y, float width, float height) {
        checkInit();
        nvgBeginPath(nvg);
        nvgRect(nvg, x, y, width, height);
        fill();
    }

    public static void strokeRect(float x, float y, float width, float height, float strokeWidth) {
        checkInit();
        nvgBeginPath(nvg);
        nvgRect(nvg, x, y, width, height);
        stroke(strokeWidth);
    }

    public static void fillPolygon(float[] vertices) {
        checkInit();
        if (vertices.length < 6) throw new IllegalArgumentException("Polygons must contain at least 3 points.");
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Polygon vertices must have even number of coordinates.");
        nvgBeginPath(nvg);
        nvgMoveTo(nvg, vertices[0], vertices[1]);
        for (int i = 2; i < vertices.length; i += 2) {
            nvgLineTo(nvg, vertices[i], vertices[i + 1]);
        }
        fill();
    }

    public static void strokePolygon(float[] vertices, float strokeWidth) {
        checkInit();
        if (vertices.length < 6) throw new IllegalArgumentException("Polygon must contain at least 3 points.");
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Polygon vertices must have even number of coordinates.");
        nvgBeginPath(nvg);
        nvgMoveTo(nvg, vertices[0], vertices[1]);
        for (int i = 2; i < vertices.length; i += 2) {
            nvgLineTo(nvg, vertices[i], vertices[i + 1]);
        }
        nvgLineTo(nvg, vertices[0], vertices[1]);
        stroke(strokeWidth);
    }

    public static void strokePolyline (float[] vertices, float strokeWidth) {
        checkInit();
        if (vertices.length < 4) throw new IllegalArgumentException("Polyline must contain at least 2 points.");
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Polyline vertices must have even number of coordinates.");
        nvgBeginPath(nvg);
        nvgMoveTo(nvg, vertices[0], vertices[1]);
        for (int i = 2; i < vertices.length; i += 2) {
            nvgLineTo(nvg, vertices[i], vertices[i + 1]);
        }
        stroke(strokeWidth);
    }

    public static void text(String text, float x, float y) {
        checkInit();
        nvgFontFace(nvg, "bahnschrift");
        nvgFillColor(nvg, color);
        nvgText(nvg, x, y, text);
    }

    public static float getTextWidth(String text, float fontSize) {
        nvgFontSize(nvg, fontSize);
        nvgFontFace(nvg, "bahnschrift");
        return nvgTextBounds(nvg, 0, 0, text, (float[]) null);
    }




}
