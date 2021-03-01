package com.chalq.math;

public class Transform2D {


    /*
        x, y = translation
        u, v = scale
        a = angle

        scale -> rotate -> translate (multiplying from right to left)
        | 1 , 0 , x |   | cos(a) , -sin(a) , 0 |   | u , 0 , 0 |
        | 0 , 1 , y | * | sin(a) ,  cos(a) , 0 | * | 0 , v , 0 |
        | 0 , 0 , 1 |   |   0    ,    0    , 1 |   | 0 , 0 , 1 |

        = | u * cos(a) , -v * sin(a) , x |
          | u * sin(a) ,  v * cos(a) , y |
          |     0      ,      0      , 1 |
     */

    final Mat3 m;
    final Vec2 translation;
    final Vec2 scale;
    float rotation;

    public Transform2D() {
        m = new Mat3();
        translation = new Vec2();
        scale = new Vec2(1, 1);
    }

    public void setRotation(float rotation) {

    }


    public float getRotation() {
        return rotation;
    }

    public void setScale(float x, float y) {

    }


    public Vec2 getScale() {
        return scale;
    }

    public void setTranslation(float x, float y) {

    }


    public Vec2 getTranslation() {
        return translation;
    }

//    public void updateTranslation() {
//        translation.x = m.m20;
//        translation.y = m.m21;
//    }
//    // always positive
//    private void updateScale() {
//        scale.x = (float) Math.sqrt( (m.m00 * m.m00) + (m.m01 * m.m01) );
//        scale.y = (float) Math.sqrt( (m.m10 * m.m10) + (m.m11 * m.m11) );
//    }
//    private void updateRotation() {
//        rotation = (float) Math.atan2(m.m01, m.m00);
//    }
}
