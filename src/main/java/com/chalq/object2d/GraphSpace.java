package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.util.Color;

public class GraphSpace extends Object2D {

    private final EmptyObject childrenTransform;
    private final Axis xAxis, yAxis;
    private float curveWidth = 5;

    private float width, height;
    private float xAxisMin, xAxisMax;
    private float yAxisMin, yAxisMax;

    public GraphSpace(float x, float y, float width, float height,
                      float xAxisMin, float xAxisMax,
                      float yAxisMin, float yAxisMax ) {

        if (xAxisMax <= xAxisMin || yAxisMax <= yAxisMin) throw new IllegalArgumentException("invalid axis min/max values!");
        childrenTransform = new EmptyObject();
        childrenTransform.setScale(
                width / (xAxisMax - xAxisMin),
                -height / (yAxisMax - yAxisMin) ); // positive is up in graph space, unlike graphics space
        childrenTransform.setPos( -xAxisMin * childrenTransform.getScaleX() , -yAxisMin * childrenTransform.getScaleY() + height );
        addChild(childrenTransform);

        setPos(x, y);
        this.width = width;
        this.height = height;

        float xVisualUnitLength = width / (xAxisMax - xAxisMin);
        float yVisualUnitLength = height / (yAxisMax - yAxisMin);
        float xMarkingWidth = 20 / yVisualUnitLength;
        float yMarkingWidth = 20 / xVisualUnitLength;
        System.out.println(xVisualUnitLength + " , " + yVisualUnitLength);
        System.out.println(xMarkingWidth + " , " + yMarkingWidth);
        xAxis = new Axis(xAxisMin, xAxisMax, 2, false, Color.WHITE, 3, xMarkingWidth);
        yAxis = new Axis(yAxisMin, yAxisMax, 2, true, Color.WHITE, 3, yMarkingWidth);
        addToGraphSpace(xAxis);
        addToGraphSpace(yAxis);

        this.xAxisMin = xAxisMin;
        this.xAxisMax = xAxisMax;
        this.yAxisMin = yAxisMin;
        this.yAxisMax = yAxisMax;
    }

    public void setAxesVisible(boolean visible) {
        xAxis.visible = visible;
    }

    @Override
    public void draw(long nvg) {

        // some axis stuff just dumping here
//        Cq.textSettings(50 * xAxis.getTraceProgress(), Cq.TextAlignH.CENTER, Cq.TextAlignV.TOP);
//        Cq.text("Soi", getX() + (width / 2 * xAxis.getTraceProgress()), getY() + height + 20 * xAxis.getTraceProgress());
//
//        Cq.textSettings(50 * yAxis.getTraceProgress(), Cq.TextAlignH.RIGHT, Cq.TextAlignV.CENTER);
//        Cq.text("Oi", getX() - 30 * yAxis.getTraceProgress(), getY() + height - height / 2 * yAxis.getTraceProgress());
    }

    @Override
    public void update() {

    }

    public void addToGraphSpace(Drawable drawable) {
        childrenTransform.addChild(drawable);
    }
}
