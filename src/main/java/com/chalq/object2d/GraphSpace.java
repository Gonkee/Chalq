package com.chalq.object2d;

import com.chalq.core.Cq;
import com.chalq.core.Object2D;
import com.chalq.core.Tween;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.util.Color;

public class GraphSpace extends Object2D implements Traceable{

    private final EmptyObject childrenTransform;

    private float width, height;
    private float minX, maxX;
    private float minY, maxY;

    private float traceProgress = 1;


    public String
            xLabel = "",
            yLabel = "";

    public float
            markingWidth = 20,
            axesStrokeWidth = 3,
            xMarkingInterval = 1,
            yMarkingInterval = 1,
            markingsFontSize = 30,
            labelFontSize = 50;


    public int
            xMarkingDecimalPlaces = 1,
            yMarkingDecimalPlaces = 1;
//    private final int
//            xMarkingCount,
//            xMinIntervalMultiple,
//            xMaxIntervalMultiple,
//
//            yMarkingCount,
//            yMinIntervalMultiple,
//            yMaxIntervalMultiple;

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

    public Vec2 graphToGlobal(float x, float y) {
        return childrenTransform.localToGlobal(x, y);
    }

    public void addToGraphSpace(Drawable drawable) {
        childrenTransform.addChild(drawable);
    }

    @Override
    public void setTraceProgress(float progress) {
        this.traceProgress = progress;
    }

    @Override
    public float getTraceProgress() {
        return traceProgress;
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }

    private static class Axes extends Object2D{

        GraphSpace gs;

        @Override
        public void draw(long nvg) {

            float xVisualUnitLength = gs.width / (gs.maxX - gs.minX);
            float yVisualUnitLength = gs.height / (gs.maxY - gs.minY);
            float xMarkingWidth = gs.markingWidth / yVisualUnitLength;
            float yMarkingWidth = gs.markingWidth / xVisualUnitLength;

            float axesProgress     = MathUtils.clamp(gs.traceProgress * 1.1f         , 0, 1); // finish early
            float markingsProgress = MathUtils.clamp(gs.traceProgress * 1.1f - (0.1f), 0, 1); // finish late

            penSetColor(gs.axesColor);
            penBeginPath(nvg);

            if (gs.xAxisVisible) {

                float midpoint = (gs.minX + gs.maxX) / 2;
                float halfDist = (gs.maxX - gs.minX) / 2;
                float buffer = halfDist / 3;
                float threshold = MathUtils.lerp( -buffer, halfDist, markingsProgress);

                penMoveTo(nvg, MathUtils.lerp(midpoint, gs.minX, axesProgress), 0);
                penLineTo(nvg, MathUtils.lerp(midpoint, gs.maxX, axesProgress), 0);

                float progress = 0;
                for (int
                     multiple =  MathUtils.ceil(gs.minX / gs.xMarkingInterval);
                     multiple <= MathUtils.floor(gs.maxX / gs.xMarkingInterval);
                     multiple ++
                ) {

                    float pos = multiple * gs.xMarkingInterval;
                    if (pos == 0 && gs.yAxisVisible) continue;

                    float currentDistance = Math.abs(pos - midpoint);
                    progress = Tween.easeInOut( 1 - (currentDistance - threshold) / buffer );

                    penMoveTo(nvg, pos, -xMarkingWidth / 2 * progress);
                    penLineTo(nvg, pos,  xMarkingWidth / 2 * progress);

                    Cq.textSettings(gs.markingsFontSize * progress, Cq.TextAlignH.CENTER, Cq.TextAlignV.TOP);
                    penText(nvg,
                            String.format("%." + Math.abs(gs.xMarkingDecimalPlaces) + "f", pos),
                            pos, -xMarkingWidth / 2 * 1.5f);
                }

                Cq.textSettings(gs.labelFontSize * progress, Cq.TextAlignH.LEFT, Cq.TextAlignV.CENTER);
                penText(nvg, gs.xLabel, gs.maxX + halfDist * 0.03f, 0);

            }


            if (gs.yAxisVisible) {

                float midpoint = (gs.minY + gs.maxY) / 2;
                float halfDist = (gs.maxY - gs.minY) / 2;
                float buffer = halfDist / 3;
                float threshold = MathUtils.lerp( -buffer, halfDist, markingsProgress);

                penMoveTo(nvg, 0, MathUtils.lerp(midpoint, gs.minY, axesProgress));
                penLineTo(nvg, 0, MathUtils.lerp(midpoint, gs.maxY, axesProgress));

                float progress = 0;
                for (int
                     multiple =  MathUtils.ceil(gs.minY / gs.yMarkingInterval);
                     multiple <= MathUtils.floor(gs.maxY / gs.yMarkingInterval);
                     multiple ++
                ) {
                    float pos = multiple * gs.yMarkingInterval;
                    if (pos == 0 && gs.xAxisVisible) continue;

                    float distFromMid = Math.abs(pos - midpoint);
                    progress = Tween.easeInOut( 1 - (distFromMid - threshold) / buffer );

                    penMoveTo(nvg, -yMarkingWidth / 2 * progress, pos);
                    penLineTo(nvg, yMarkingWidth / 2 * progress, pos);

                    Cq.textSettings(gs.markingsFontSize * progress, Cq.TextAlignH.LEFT, Cq.TextAlignV.CENTER);
                    penText(nvg,
                            String.format("%." + Math.abs(gs.yMarkingDecimalPlaces) + "f", pos),
                            yMarkingWidth / 2 * 1.5f, pos);
                }

                Cq.textSettings(gs.labelFontSize * progress, Cq.TextAlignH.CENTER, Cq.TextAlignV.BOTTOM);
                penText(nvg, gs.yLabel, 0, gs.maxY + halfDist * 0.03f);
            }

            penStrokePath(nvg, gs.axesStrokeWidth);
        }

        @Override
        public void update() {

        }
    }

}
