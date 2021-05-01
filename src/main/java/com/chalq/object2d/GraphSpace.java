package com.chalq.object2d;

import com.chalq.core.Object2D;

public class GraphSpace extends Object2D {

    private final EmptyObject childrenTransform;
    private final Arrow xAxisPos, yAxisPos, xAxisNeg, yAxisNeg;
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
        xAxisPos = new Arrow(0, 0, xAxisMax, 0, 0, curveWidth);
        yAxisPos = new Arrow(0, 0, 0, yAxisMax, 0, curveWidth);
        xAxisNeg = new Arrow(0, 0, xAxisMin, 0, 0, curveWidth);
        yAxisNeg = new Arrow(0, 0, 0, yAxisMin, 0, curveWidth);
        addToGraphSpace(xAxisPos);
        addToGraphSpace(yAxisPos);
        addToGraphSpace(xAxisNeg);
        addToGraphSpace(yAxisNeg);

        this.xAxisMin = xAxisMin;
        this.xAxisMax = xAxisMax;
        this.yAxisMin = yAxisMin;
        this.yAxisMax = yAxisMax;
    }

    public void setAxesVisible(boolean visible) {
        xAxisPos.visible = visible;
        yAxisPos.visible = visible;
        xAxisNeg.visible = visible;
        yAxisNeg.visible = visible;
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
