package com.chalq.drawables.graph;

import com.chalq.core.Cq;
import com.chalq.drawables.Drawable;
import com.chalq.math.Vec2;

import java.util.function.Function;

public class GraphPlotter implements Drawable {

    private final Function<Float, Vec2> parametricFunction;

    private float x, y, width, height;
    private float xAxisMin, xAxisMax;
    private float yAxisMin, yAxisMax;

    private float minInput;
    private float maxInput;

    float[] line = new float[1000];

    public static Function<Float, Vec2> toParametric(Function<Float, Float> function, boolean inverse) {
        if (inverse) {
            return (t) -> new Vec2(function.apply(t), t);
        } else {
            return (t) -> new Vec2(t, function.apply(t));
        }
    }

    public GraphPlotter(float x, float y, float width, float height,
                        Function<Float, Vec2> parametricFunction,
                        float minInput, float maxInput,
                        float xAxisMin, float xAxisMax,
                        float yAxisMin, float yAxisMax )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.xAxisMin = xAxisMin;
        this.xAxisMax = xAxisMax;
        this.yAxisMin = yAxisMin;
        this.yAxisMax = yAxisMax;

        this.parametricFunction = parametricFunction;
        this.minInput = minInput;
        this.maxInput = maxInput;
        updateGraph();
    }

    private void updateGraph() {
        for (int i = 0; i < line.length; i += 2) {
            Vec2 result = parametricFunction.apply(i * 0.01f);
            line[i] = 960 + result.x * 20;
            line[i + 1] = 500 + result.y * 20;
        }
    }

    @Override
    public void draw() {
        updateGraph();
        Cq.strokePolyline(line, 5);
    }
}
