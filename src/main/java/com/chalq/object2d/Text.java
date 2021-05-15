package com.chalq.object2d;

import com.chalq.core.Cq;
import com.chalq.core.IOUtils;
import com.chalq.core.Object2D;
import com.chalq.util.Color;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.nanovg.NanoVG.*;

public class Text extends Object2D {

    public String text;

    public float offsetX, offsetY, fontSize = 20;
    public Cq.TextAlignH alignH = Cq.TextAlignH.CENTER;
    public Cq.TextAlignV alignV = Cq.TextAlignV.CENTER;

    public Color color = Color.WHITE;

    private static void init() {
    }

    public Text(String text) {
        this.text = text;
    }

    @Override
    public void draw(long nvg) {
        Cq.textSettings(fontSize * getScaleX(), alignH, alignV);
        penBeginPath(nvg);
        penSetColor(Color.WHITE);
        penText(nvg, text, offsetX, offsetY);
    }

    @Override
    public void update() {

    }
}
