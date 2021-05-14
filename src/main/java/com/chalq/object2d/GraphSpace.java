package com.chalq.object2d;

import com.chalq.core.Cq;
import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.util.Color;

public class GraphSpace extends Object2D {

    private final EmptyObject childrenTransform;

    private float width, height;
    private float minX, maxX;
    private float minY, maxY;

    private final float
            xMarkingWidth,
            yMarkingWidth,
            axesStrokeWidth = 3,
            axesMarkingInterval = 2;

    private final int
            xMarkingCount,
            xMinIntervalMultiple,
            xMaxIntervalMultiple,

            yMarkingCount,
            yMinIntervalMultiple,
            yMaxIntervalMultiple;

    public boolean
            xAxisVisible = true,
            yAxisVisible = true;

    private Color axesColor = Color.WHITE;

    public GraphSpace(float x, float y, float width, float height,
                      float minX, float maxX, float minY, float maxY ) {

        if (maxX <= minX || maxY <= minY) throw new IllegalArgumentException("invalid axis min/max values!");
        childrenTransform = new EmptyObject();
        childrenTransform.setScale(
                width / (maxX - minX),
                -height / (maxY - minY) ); // positive is up in graph space, unlike graphics space
        childrenTransform.setPos( -minX * childrenTransform.getScaleX() , -minY * childrenTransform.getScaleY() + height );
        addChild(childrenTransform);

        setPos(x, y);
        this.width = width;
        this.height = height;

        float xVisualUnitLength = width / (maxX - minX);
        float yVisualUnitLength = height / (maxY - minY);
        xMarkingWidth = 20 / yVisualUnitLength;
        yMarkingWidth = 20 / xVisualUnitLength;
        System.out.println(xVisualUnitLength + " , " + yVisualUnitLength);
        System.out.println(xMarkingWidth + " , " + yMarkingWidth);
//        xAxis = new Axis(minX, maxX, 2, false, Color.WHITE, 3, xMarkingWidth);
//        yAxis = new Axis(minY, maxY, 2, true, Color.WHITE, 3, yMarkingWidth);
//        addToGraphSpace(xAxis);
//        addToGraphSpace(yAxis);


        // interval multiple = number that interval is multiplied by to get the min/max
        xMinIntervalMultiple = MathUtils.ceil(minX / axesMarkingInterval);
        xMaxIntervalMultiple = MathUtils.floor(maxX / axesMarkingInterval);
        yMinIntervalMultiple = MathUtils.ceil(minY / axesMarkingInterval);
        yMaxIntervalMultiple = MathUtils.floor(maxY / axesMarkingInterval);

        xMarkingCount = xMaxIntervalMultiple - xMinIntervalMultiple + 1;
        yMarkingCount = yMaxIntervalMultiple - yMinIntervalMultiple + 1;

        Axes axes = new Axes();
        axes.gs = this;
        addToGraphSpace(axes);

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
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

    private static class Axes extends Object2D{

        GraphSpace gs;

        @Override
        public void draw(long nvg) {
            penSetColor(gs.axesColor);
            penBeginPath(nvg);

            if (gs.xAxisVisible) {
                Cq.textSettings(30, Cq.TextAlignH.CENTER, Cq.TextAlignV.TOP);
                penMoveTo(nvg, gs.minX, 0);
                penLineTo(nvg, gs.maxX, 0);
                for (int i = 0; i < gs.xMarkingCount; i++) {
                    float pos = (gs.xMinIntervalMultiple + i) * gs.axesMarkingInterval;
                    penMoveTo(nvg, pos, -gs.xMarkingWidth / 2);
                    penLineTo(nvg, pos, gs.xMarkingWidth / 2);
                    penText(nvg, "" + pos, pos, -gs.xMarkingWidth / 2 * 1.5f);
                }
            }


            if (gs.yAxisVisible) {
                Cq.textSettings(30, Cq.TextAlignH.LEFT, Cq.TextAlignV.CENTER);
                penMoveTo(nvg, 0, gs.minY);
                penLineTo(nvg, 0, gs.maxY);
                for (int i = 0; i < gs.yMarkingCount; i++) {
                    float pos = (gs.yMinIntervalMultiple + i) * gs.axesMarkingInterval;
                    penMoveTo(nvg, -gs.yMarkingWidth / 2, pos);
                    penLineTo(nvg, gs.yMarkingWidth / 2, pos);
                    penText(nvg, "" + pos, gs.yMarkingWidth / 2 * 1.5f, pos);
                }
            }

            penStrokePath(nvg, gs.axesStrokeWidth);
        }

        @Override
        public void update() {

        }
    }

}
