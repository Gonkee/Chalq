package com.chalq.object2d;

import com.chalq.core.Object2D;
import com.chalq.core.Tween;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.util.Color;

public class VectorField extends Object2D implements Traceable{

    private final int xCount, yCount; // columns (no. of x values), rows (no. of y values)
    private final float interval, maxLength, arrowWidth;
    private final float[] posX;
    private final float[] posY;
    private final float[][] vecX;
    private final float[][] vecY;
    private final Arrow[][] arrows;
    private final Color color;

    private float traceProgress = 1;


    public VectorField(float minX, float maxX, float minY, float maxY, float interval, float maxLength, Color color, float arrowWidth) {

        this.color = color;
        this.interval = interval;
        this.maxLength = maxLength;
        this.arrowWidth = arrowWidth;

        // interval multiple = number that interval is multiplied by to get the min/max
        int minIntervalMultipleX = MathUtils.ceil(minX / interval);
        int maxIntervalMultipleX = MathUtils.floor(maxX / interval);
        int minIntervalMultipleY = MathUtils.ceil(minY / interval);
        int maxIntervalMultipleY = MathUtils.floor(maxY / interval);

        xCount = maxIntervalMultipleX - minIntervalMultipleX + 1;
        yCount = maxIntervalMultipleY - minIntervalMultipleY + 1;

        posX = new float[xCount];
        posY = new float[yCount];
        vecX = new float[xCount][yCount];
        vecY = new float[xCount][yCount];
        arrows = new Arrow[xCount][yCount];

        for (int x = 0; x < xCount; x++) {
            posX[x] = (minIntervalMultipleX + x) * interval;
        }
        for (int y = 0; y < yCount; y++) {
            posY[y] = (minIntervalMultipleY + y) * interval;
        }
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                arrows[x][y] = new Arrow(posX[x], posY[y], 0, 0, 0, arrowWidth);
                arrows[x][y].setColor(color);
                addChild(arrows[x][y]);
            }
        }
    }

    public int xCount() {
        return xCount;
    }

    public int yCount() {
        return yCount;
    }

    public float getX(int xIndex) {
        return posX[xIndex];
    }

    public float getY(int yIndex) {
        return posY[yIndex];
    }

    public float getVecX(int xIndex, int yIndex) {
        return vecX[xIndex][yIndex];
    }

    public float getVecY(int xIndex, int yIndex) {
        return vecY[xIndex][yIndex];
    }

    public void setVec(int xIndex, int yIndex, float vecX, float vecY) {
        this.vecX[xIndex][yIndex] = vecX;
        this.vecY[xIndex][yIndex] = vecY;

        float length = (float) Math.sqrt( (vecX * vecX) + (vecY * vecY) );
        float sizeProportion = MathUtils.clamp(length / maxLength, 0, 1);

        float adjustedVectorSize = MathUtils.lerp(interval * 0.3f, interval * 0.6f, sizeProportion);
        float adjustedArrowWidth = MathUtils.lerp(arrowWidth * 0.5f, arrowWidth, sizeProportion);

        arrows[xIndex][yIndex].setPointX(vecX / length * adjustedVectorSize);
        arrows[xIndex][yIndex].setPointY(vecY / length * adjustedVectorSize);
        arrows[xIndex][yIndex].setWidth(adjustedArrowWidth);
    }


    @Override
    public void draw(long nvg) {

    }

    @Override
    public void update() {

    }

    @Override
    public void setTraceProgress(float progress) {
        this.traceProgress = progress;

        float bottomLeft = posX[0] + posY[0];
        float topRight = posX[xCount - 1] + posY[yCount - 1];

        float buffer = (topRight - bottomLeft) / 3;
        float threshold = MathUtils.lerp(bottomLeft - buffer, topRight, traceProgress);

        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                arrows[x][y].setTraceProgress(
                        Tween.easeInOut(
                                1 - (posX[x] + posY[y] - threshold) / buffer
                        )
                );
            }
        }

    }

    @Override
    public float getTraceProgress() {
        return traceProgress;
    }

    @Override
    public Vec2 getLocalTracePosition() {
        return null;
    }
}
