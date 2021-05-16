package com.chalq.math;

public class Vec3 {

    public float x, y, z;

    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 v) {
        set(v);
    }

    public static Vec3 cross(Vec3 a, Vec3 b, Vec3 result) {
        if (result == null) result = new Vec3();
        result.set(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
        return result;
    }

    public static float dot(Vec3 a, Vec3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public Vec3 transform(Mat4 m) {
        float tx = x * m.m00 + y * m.m10 + z * m.m20 + m.m30;
        float ty = x * m.m01 + y * m.m11 + z * m.m21 + m.m31;
        float tz = x * m.m02 + y * m.m12 + z * m.m22 + m.m32;
        x = tx;
        y = ty;
        z = tz;
        return this;
    }

    public Vec3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vec3 set(Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }

    public float len () {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 scl(float fac) {
        x *= fac;
        y *= fac;
        z *= fac;
        return this;
    }

    public Vec3 nor() {
        float len = len();
        if (len != 0) {
            x /= len;
            y /= len;
            z /= len;
        }
        return this;
    }

}
