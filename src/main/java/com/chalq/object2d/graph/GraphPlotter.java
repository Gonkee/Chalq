package com.chalq.object2d.graph;

import com.chalq.core.Cq;
import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.object2d.Arrow;
import com.chalq.object2d.Traceable;
import com.chalq.object2d.path2d.PolyPath;
import com.chalq.util.Color;

import java.util.ArrayList;
import java.util.function.Function;

public class GraphPlotter extends Object2D implements Traceable {

    private final ArrayList<Function<Float, Vec2>> functions = new ArrayList<>();
    private final ArrayList<Float> minInputs = new ArrayList<>();
    private final ArrayList<Float> maxInputs = new ArrayList<>();
    private final ArrayList<PolyPath> curves = new ArrayList<>();
    private final ArrayList<Boolean> liveUpdateFlags = new ArrayList<>();

    private final Arrow xAxis, yAxis;

    private float width, height;
    private float xAxisMin, xAxisMax;
    private float yAxisMin, yAxisMax;

    private float curveWidth = 5;
    private int levelOfDetail = 100;

    public static Function<Float, Vec2> toParametric(Function<Float, Float> function, boolean inverse) {
        if (inverse) {
            return (t) -> new Vec2(function.apply(t), t);
        } else {
            return (t) -> new Vec2(t, function.apply(t));
        }
    }

    public GraphPlotter(float x, float y, float width, float height,
                        float xAxisMin, float xAxisMax,
                        float yAxisMin, float yAxisMax )
    {
        setPos(x, y);
        this.width = width;
        this.height = height;
        xAxis = new Arrow(0, height, width, 0, 0, curveWidth);
        yAxis = new Arrow(0, height, 0, -height, 0, curveWidth);
        addChild(xAxis);
        addChild(yAxis);

        this.xAxisMin = xAxisMin;
        this.xAxisMax = xAxisMax;
        this.yAxisMin = yAxisMin;
        this.yAxisMax = yAxisMax;
    }

    public void setAxesVisible(boolean visible) {
        xAxis.visible = visible;
        yAxis.visible = visible;
    }

    public int addFunction(Function<Float, Vec2> function, float minInput, float maxInput, Color color, boolean liveUpdate) {
        int newFunctionID = functions.size();
        functions.add(function);
        minInputs.add(minInput);
        maxInputs.add(maxInput);
        liveUpdateFlags.add(liveUpdate);

        PolyPath newPath = new PolyPath(new float[levelOfDetail * 2]);
        newPath.color = color;
        addChild(newPath);
        curves.add(newPath);

        updateGraph(newFunctionID);
        return newFunctionID;
    }


    private void updateGraph(int index) {
        float vx, vy;
        for (int vID = 0; vID < levelOfDetail; vID++) {
            float input = MathUtils.lerp(minInputs.get(index), maxInputs.get(index), (float) vID / levelOfDetail);
            Vec2 result = functions.get(index).apply(input);
            vx = (result.x - xAxisMin) / (xAxisMax - xAxisMin) * width;
            vy = height - (result.y - yAxisMin) / (yAxisMax - yAxisMin) * height;
            curves.get(index).setVertexCoord(vID * 2    , vx);
            curves.get(index).setVertexCoord(vID * 2 + 1, vy);
        }
    }

    @Override
    public void draw(long nvg) {
        for (int i = 0; i < functions.size(); i++) {
            curves.get(i).strokeWidth = curveWidth;
        }

        // temp
//        Cq.textSettings(50 * xAxis.getTraceProgress(), Cq.TextAlignH.CENTER, Cq.TextAlignV.TOP);
//        Cq.text("Soi", getX() + (width / 2 * xAxis.getTraceProgress()), getY() + height + 20 * xAxis.getTraceProgress());
//
//        Cq.textSettings(50 * yAxis.getTraceProgress(), Cq.TextAlignH.RIGHT, Cq.TextAlignV.CENTER);
//        Cq.text("Oi", getX() - 30 * yAxis.getTraceProgress(), getY() + height - height / 2 * yAxis.getTraceProgress());
    }

    @Override
    public void update() {
        for (int i = 0; i < functions.size(); i++) {
            if (liveUpdateFlags.get(i)) updateGraph(i);
        }
    }

    @Override
    public void setTraceProgress(float progress) {
        xAxis.setTraceProgress(progress);
        yAxis.setTraceProgress(progress);
        for (PolyPath c : curves) {
            c.setTraceProgress(progress);
        }
    }

    @Override
    public float getTraceProgress() {
        return 0;
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
