package com.chalq.drawables;

import com.chalq.util.Color;
import com.chalq.util.MathUtils;

import java.util.ArrayList;

import static com.chalq.core.Cq.*;

public class LineChart implements Drawable {

    private static final int MAX_GRAPH_DETAIL = 100;

    public final boolean stack;
    public final String xAxisLabel;
    public final String yAxisLabel;
    public boolean fill = false;
    public boolean yAxisStartAtZero = false;
    public boolean topAndBottomSpace = false;
    public float minXUnitsToFillGraph = 10;
    public int entriesPerXUnit = 1;

    private Color[] colors = null;
    private ArrayList<Float> data = new ArrayList<>();
    private float yAxisMaxValue = Float.MIN_VALUE;
    private float yAxisMinValue = Float.MAX_VALUE;

    private float x, y, width, height;

    public LineChart (float x, float y, float width, float height, String xAxisLabel, String yAxisLabel, boolean stack) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.stack = stack;
        if (stack) yAxisMinValue = 0;
    }

    public void initLines(Color... colors) {
        if (this.colors == null) this.colors = colors;
        else new IllegalStateException("Lines have already been initialized, cannot be initialized again").printStackTrace();
    }

    public void addData(float... data) {
        if (colors == null) {
            new IllegalStateException("LineChart lines have not been initialized, cannot add data - must call initLines()").printStackTrace();
            return;
        }
        if (data.length != colors.length) new IllegalArgumentException("Number of data entries doesn't match number of lines!").printStackTrace();

        float total = 0;
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (float f : data) {
            max = Math.max(max, f);
            min = Math.min(min, f);
            total += f;
            if (stack) this.data.add(total);
            else this.data.add(f);
        }
        if (stack) {
            yAxisMaxValue = Math.max(yAxisMaxValue, total);
        }
        else {
            yAxisMaxValue = Math.max(yAxisMaxValue, max);
            yAxisMinValue = Math.min(yAxisMinValue, min);
        }

    }


    @Override
    public void draw() {

        // setting the y axis scale
        float yAxisBottom;
        float yAxisTop;
        if (yAxisMaxValue == yAxisMinValue) {
            if (yAxisMaxValue == 0) {
                yAxisBottom = -1;
                yAxisTop = 1;
            } else {
                yAxisBottom = yAxisMaxValue - Math.abs(yAxisMaxValue);
                yAxisTop = yAxisMaxValue + Math.abs(yAxisMaxValue);
            }
        } else {

            if (yAxisStartAtZero && yAxisMaxValue >= 0 && yAxisMinValue >= 0) {
                yAxisBottom = 0;
                yAxisTop = yAxisMaxValue;
            } else if (yAxisStartAtZero && yAxisMaxValue <= 0 && yAxisMinValue <= 0) {
                yAxisBottom = yAxisMinValue;
                yAxisTop = 0;
            } else {
                yAxisBottom = yAxisMinValue;
                yAxisTop = yAxisMaxValue;
            }

            if (topAndBottomSpace) {
                float spaceOffset = (yAxisTop - yAxisBottom) * 0.1f;
                yAxisTop += spaceOffset;
                yAxisBottom -= spaceOffset;
            }
        }


        final int numDataPoints = data.size() / colors.length;
        final int numVertices = Math.min(numDataPoints, MAX_GRAPH_DETAIL);

        float contentHorizontalScale = (width / numDataPoints);
        float scaleCap = minXUnitsToFillGraph > 0 ? width / minXUnitsToFillGraph : Float.MAX_VALUE;
        if (contentHorizontalScale > scaleCap) contentHorizontalScale = scaleCap;
        float contentHorizontalBounds = numDataPoints * contentHorizontalScale;

        if (numVertices > 1) {
            for (int i = colors.length - 1; i >= 0; i--) {
                setColor(colors[i]);

                float[] vertices;
                if (fill) vertices = new float[(numVertices + 2) * 2];
                else vertices = new float[numVertices * 2];

                int p = 0;
                for (int g = 0; g < numVertices; g++) {
                    vertices[p++] = x + (contentHorizontalBounds * g / (numVertices - 1) ); // x coordinate

                    float dataPos = (float) g / numVertices * numDataPoints;
                    int lDataID = MathUtils.floor(dataPos);
                    int rDataID = MathUtils.clamp(lDataID + 1, 0, numDataPoints - 1);
                    float lerpFac = MathUtils.fract(dataPos);

                    float finalDataSample = MathUtils.lerp( data.get(lDataID * colors.length + i), data.get(rDataID * colors.length + i), lerpFac);
                    float verticalPos = (finalDataSample - yAxisBottom) / (yAxisTop - yAxisBottom);
                    vertices[p++] = y + height - (verticalPos * height);
                }
                if (fill) {
                    vertices[p++] = x + contentHorizontalBounds;
                    vertices[p++] = y + height;
                    vertices[p++] = x;
                    vertices[p++] = y + height;
                    fillPolygon(vertices);
                } else strokePolyline(vertices, 3);
            }
        }

        setColor(Color.WHITE);
        float lineWidth = 5;
        line(x, y, x, y + height, lineWidth);
        line(x, y + height, x + width, y + height, lineWidth);

        int closestPowOf10 = (int) (Math.log10(yAxisTop - yAxisBottom) - 0.5);
        float increment = (float) Math.pow(10, closestPowOf10);
        while (increment < (yAxisTop - yAxisBottom) / 8) increment *= 2;

        textSettings(30, TextAlignH.RIGHT, TextAlignV.CENTER);
        for (int i = MathUtils.floor(yAxisMinValue / increment); (i - 1) * increment < yAxisMaxValue; i++ ) {
            float value = increment * i;
            float verticalPos = (value - yAxisBottom) / (yAxisTop - yAxisBottom);
            if (verticalPos <= 1 && verticalPos >= 0) {
                float yValue = y + height - (verticalPos * height);
                line(x - 20, yValue, x, yValue, lineWidth);
                text("" + value, x - 30, yValue);
            }
        }

        int numXUnits = numDataPoints / entriesPerXUnit;
        int xAxisIncrement = 1;
        boolean x5 = true;
        while (numXUnits / xAxisIncrement > 9) {
            xAxisIncrement *= x5 ? 5 : 2;
            x5 = !x5;
        }

        textSettings(30, TextAlignH.CENTER, TextAlignV.TOP);
        for (int i = 0; i <= numXUnits; i += xAxisIncrement) {
            float horizontalPos = (float) i * entriesPerXUnit / (numDataPoints - 1);
            if (horizontalPos >= 0 && horizontalPos <= 1) {
                float xPos = x + (contentHorizontalBounds * horizontalPos); // x coordinate
                line(xPos, y + height, xPos, y + height + 20, lineWidth);
                text("" + i, xPos, y + height + 30);
            }
        }

        textSettings(40, TextAlignH.CENTER, TextAlignV.BOTTOM);
        text(yAxisLabel, x, y - 30);
        textSettings(40, TextAlignH.CENTER, TextAlignV.TOP);
        text(xAxisLabel, x + width / 2, y + height + 70);

    }

}
