package com.chalq.core;

import com.chalq.util.Color;
import org.lwjgl.nanovg.NVGColor;

import java.nio.ByteBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;

public class Cq {

    private Cq() {}

    public static float time = 0;
    public static float delta = 0;

    private static boolean initialized = false;
    private static long nvg;
    private static NVGColor color;
//    private static NVGColor bgColor;

    protected static void init(long nvg, Color bgColor) {
        initialized = true;

        Cq.nvg = nvg;
        glClearColor(bgColor.r, bgColor.g, bgColor.b, 1);
        color = NVGColor.create();
        color.r(1);
        color.g(1);
        color.b(1);
        color.a(1);

        ByteBuffer fontBuffer = IOUtils.ioResourceToByteBuffer("bahnschrift.ttf", 40 * 1024);
        int font = nvgCreateFontMem(nvg, "bahnschrift", fontBuffer, 0);
    }

    private static void checkInit() {
        if (!initialized) throw new IllegalStateException("Chalq has not been initialized! Create a CqWindow to initialize Chalq");
    }

    private static void fill() {
        nvgFillColor(nvg, color);
        nvgFill(nvg);
    }

    private static void stroke(float strokeWidth) {
        nvgStrokeColor(nvg, color);
        nvgStrokeWidth(nvg, strokeWidth);
        nvgStroke(nvg);
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

    public static void text(String text, float x, float y, float fontSize) {
        checkInit();
        nvgBeginPath(nvg);
        nvgFontSize(nvg, fontSize);
        nvgFontFace(nvg, "bahnschrift");
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgFillColor(nvg, color);
        nvgText(nvg, x, y, text);
    }

    public static float getTextWidth(String text, float fontSize) {
        nvgFontSize(nvg, fontSize);
        nvgFontFace(nvg, "bahnschrift");
        return nvgTextBounds(nvg, 0, 0, text, (float[]) null);
    }




}
