package com.chalq.object2d;

import com.chalq.core.Cq;
import com.chalq.core.IOUtils;
import com.chalq.core.Object2D;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.nanovg.NanoVG.*;

public class Text extends Object2D {


    public enum AlignH { LEFT, CENTER, RIGHT }
    public enum AlignV { TOP, CENTER, BOTTOM }

    private static int alignH = NVG_ALIGN_CENTER;
    private static int alignV = NVG_ALIGN_MIDDLE;
    public String text;

    private static void init() {
    }

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void draw(long nvg) {
//        Cq.text();
    }

    @Override
    public void update() {

    }
}
