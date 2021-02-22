package com.chalq.drawables.graph;

import com.chalq.core.Cq;
import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.util.Color;

import java.util.ArrayList;
import java.util.function.Function;

public class GraphPlotter extends Object2D {

    private final ArrayList<Function<Float, Vec2>> functions = new ArrayList<>();
    private final ArrayList<Float> minInputs = new ArrayList<>();
    private final ArrayList<Float> maxInputs = new ArrayList<>();
    private final ArrayList<float[]> curves = new ArrayList<>();
    private final ArrayList<Color> curveColors = new ArrayList<>();
    private final ArrayList<Boolean> liveUpdateFlags = new ArrayList<>();

    private float width, height;
    private float xAxisMin, xAxisMax;
    private float yAxisMin, yAxisMax;

    private float curveWidth = 4;
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
        this.pos.x = x;
        this.pos.y = y;
        this.width = width;
        this.height = height;

        this.xAxisMin = xAxisMin;
        this.xAxisMax = xAxisMax;
        this.yAxisMin = yAxisMin;
        this.yAxisMax = yAxisMax;
    }

    public int addFunction(Function<Float, Vec2> function, float minInput, float maxInput, Color color, boolean liveUpdate) {
        int newFunctionID = functions.size();
        functions.add(function);
        minInputs.add(minInput);
        maxInputs.add(maxInput);
        curves.add(new float[levelOfDetail * 2]);
        curveColors.add(color);
        liveUpdateFlags.add(liveUpdate);
        updateGraph(newFunctionID);
        return newFunctionID;
    }


    private void updateGraph(int index) {
        for (int vID = 0; vID < levelOfDetail; vID++) {
            float input = MathUtils.lerp(minInputs.get(index), maxInputs.get(index), (float) vID / levelOfDetail);
            Vec2 result = functions.get(index).apply(input);
            curves.get(index) [vID * 2    ] = pos.x + (result.x - xAxisMin) / (xAxisMax - xAxisMin) * width;
            curves.get(index) [vID * 2 + 1] = pos.y + height - (result.y - yAxisMin) / (yAxisMax - yAxisMin) * height;
        }
    }

    @Override
    protected void draw() {
        for (int i = 0; i < functions.size(); i++) {
            Cq.setColor(curveColors.get(i));
            Cq.strokePolyline(curves.get(i), curveWidth);
        }
    }

    @Override
    protected void update() {
        for (int i = 0; i < functions.size(); i++) {
            if (liveUpdateFlags.get(i)) updateGraph(i);
        }
    }
}
