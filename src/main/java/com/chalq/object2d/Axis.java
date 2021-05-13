package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.math.MathUtils;
import com.chalq.util.Color;

public class Axis extends Object2D {

    private float minValue, maxValue, interval, strokeWidth, markingWidth;
    private int markingCount, minIntervalMultiple, maxIntervalMultiple;

    private boolean vertical;

    public Axis(float minValue, float maxValue, float interval, boolean vertical, Color color, float strokeWidth, float markingWidth) {

        // interval multiple = number that interval is multiplied by to get the min/max
        minIntervalMultiple = MathUtils.ceil(minValue / interval);
        maxIntervalMultiple = MathUtils.floor(maxValue / interval);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.interval = interval;
        this.vertical = vertical;
        this.strokeWidth = strokeWidth;
        this.markingWidth = markingWidth;

        markingCount = maxIntervalMultiple - minIntervalMultiple + 1;
    }



    @Override
    public void draw(long nvg) {

        penBeginPath(nvg);
        if (!vertical) {
            penMoveTo(nvg, minValue, 0);
            penLineTo(nvg, maxValue, 0);

            float pos;
            for (int i = 0; i < markingCount; i++) {
                pos = (minIntervalMultiple + i) * interval;
                penMoveTo(nvg, pos, -markingWidth / 2);
                penLineTo(nvg, pos,  markingWidth / 2);
            }

        } else {
            penMoveTo(nvg, 0, minValue);
            penLineTo(nvg, 0, maxValue);

            float pos;
            for (int i = 0; i < markingCount; i++) {
                pos = (minIntervalMultiple + i) * interval;
                penMoveTo(nvg, -markingWidth / 2, pos);
                penLineTo(nvg,  markingWidth / 2, pos);
            }
        }
        penStrokePath(nvg, strokeWidth);
    }

    @Override
    public void update() {

    }
}
