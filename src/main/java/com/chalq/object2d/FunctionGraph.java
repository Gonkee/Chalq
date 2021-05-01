package com.chalq.object2d;

import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;
import com.chalq.util.Color;

import java.util.function.Function;

public class FunctionGraph extends PolyPath{


    public static Function<Float, Vec2> toParametric(Function<Float, Float> function, boolean inverse) {
        if (inverse) {
            return (t) -> new Vec2(function.apply(t), t);
        } else {
            return (t) -> new Vec2(t, function.apply(t));
        }
    }

    Function<Float, Vec2> function;
    float minInput, maxInput;
    boolean liveUpdate;
    int segments;

    public FunctionGraph(Function<Float, Float> function,
                         float minInput, float maxInput,
                         Color color, int segments,
                         boolean liveUpdate, boolean inverse)
    {
        this( !inverse ? (t) -> new Vec2(t, function.apply(t)) : (t) -> new Vec2(function.apply(t), t) ,
                minInput, maxInput, color, segments, liveUpdate );
    }

    public FunctionGraph(Function<Float, Vec2> function,
                         float minInput, float maxInput,
                         Color color, int segments, boolean liveUpdate)
    {
        super(new float[segments * 2]);
        this.function = function;
        this.minInput = minInput;
        this.maxInput = maxInput;
        this.color = color;
        this.segments = segments;

        this.liveUpdate = true;
        update();
        this.liveUpdate = liveUpdate;
    }

    @Override
    public void update() {
        if (!liveUpdate) return;
        for (int vID = 0; vID < segments; vID++) {
            float input = MathUtils.lerp(minInput, maxInput, (float) vID / segments);
            Vec2 result = function.apply(input);
            setVertexCoord(vID * 2    , result.x);
            setVertexCoord(vID * 2 + 1, result.y);
        }
    }
}
