package com.chalq.drawables;

import com.chalq.core.Cq;
import com.chalq.core.Drawable;
import com.chalq.math.Vec2;

public class Arrow extends Drawable {

    public float width;
    public final Vec2 pointVector = new Vec2();
    public float arcAngle = 0;

    public Arrow(float x, float y, float pointX, float pointY, float width) {
        this.pos.x = x;
        this.pos.y = y;
        this.pointVector.x = pointX;
        this.pointVector.y = pointY;
        this.width = width;
    }

    @Override
    protected void draw() {
        if (arcAngle > 0) {
            Cq.arcClockwise(pos.x, pos.y, pos.x + pointVector.x, pos.y + pointVector.y, arcAngle, width);
        } else if (arcAngle < 0) {
            Cq.arcClockwise(pos.x + pointVector.x, pos.y + pointVector.y, pos.x, pos.y, -arcAngle, width);
        } else {
            Cq.line(pos.x, pos.y, pos.x + pointVector.x, pos.y + pointVector.y, width);
        }


        float rotationDueToArc = 180 - 90 - (180 - arcAngle) / 2;

        Vec2 tipDir = new Vec2(pointVector).nor().rotate(rotationDueToArc);

        Vec2 tipV1 = new Vec2(tipDir).scl(width * 2).add(pos).add(pointVector);
        Vec2 tipV2 = new Vec2(tipDir).scl(width * 2).rotate(-120).add(pos).add(pointVector);
        Vec2 tipV3 = new Vec2(tipDir).scl(width * 2).rotate( 120).add(pos).add(pointVector);
        Cq.fillPolygon(new float[] {tipV1.x, tipV1.y, tipV2.x, tipV2.y, tipV3.x, tipV3.y});
    }

    @Override
    protected void update() {

    }
}
