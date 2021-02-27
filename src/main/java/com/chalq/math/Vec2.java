package com.chalq.math;

public class Vec2 {

    /** the x-component of this vector **/
    public float x;
    /** the y-component of this vector **/
    public float y;

    /** Constructs a new vector at (0,0) */
    public Vec2 () {
    }

    /** Constructs a vector with the given components
     * @param x The x-component
     * @param y The y-component */
    public Vec2 (float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Constructs a vector from the given vector
     * @param v The vector */
    public Vec2 (Vec2 v) {
        set(v);
    }

    
    public Vec2 cpy () {
        return new Vec2(this);
    }

    public static float len (float x, float y) {
        return (float)Math.sqrt(x * x + y * y);
    }

    
    public float len () {
        return (float)Math.sqrt(x * x + y * y);
    }

    public static float len2 (float x, float y) {
        return x * x + y * y;
    }

    
    public float len2 () {
        return x * x + y * y;
    }

    
    public Vec2 set (Vec2 v) {
        x = v.x;
        y = v.y;
        return this;
    }

    /** Sets the components of this vector
     * @param x The x-component
     * @param y The y-component
     * @return This vector for chaining */
    public Vec2 set (float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    
    public Vec2 sub (Vec2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    /** Substracts the other vector from this vector.
     * @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @return This vector for chaining */
    public Vec2 sub (float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    
    public Vec2 nor () {
        float len = len();
        if (len != 0) {
            x /= len;
            y /= len;
        }
        return this;
    }

    
    public Vec2 add (Vec2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    /** Adds the given components to this vector
     * @param x The x-component
     * @param y The y-component
     * @return This vector for chaining */
    public Vec2 add (float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public static float dot (float x1, float y1, float x2, float y2) {
        return x1 * x2 + y1 * y2;
    }

    
    public float dot (Vec2 v) {
        return x * v.x + y * v.y;
    }

    public float dot (float ox, float oy) {
        return x * ox + y * oy;
    }

    
    public Vec2 scl (float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    /** Multiplies this vector by a scalar
     * @return This vector for chaining */
    public Vec2 scl (float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    
    public Vec2 scl (Vec2 v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    
    public Vec2 mulAdd (Vec2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    
    public Vec2 mulAdd (Vec2 vec, Vec2 mulVec) {
        this.x += vec.x * mulVec.x;
        this.y += vec.y * mulVec.y;
        return this;
    }

    public static float dst (float x1, float y1, float x2, float y2) {
        final float x_d = x2 - x1;
        final float y_d = y2 - y1;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    
    public float dst (Vec2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    /** @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @return the distance between this and the other vector */
    public float dst (float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return (float)Math.sqrt(x_d * x_d + y_d * y_d);
    }

    public static float dst2 (float x1, float y1, float x2, float y2) {
        final float x_d = x2 - x1;
        final float y_d = y2 - y1;
        return x_d * x_d + y_d * y_d;
    }

    
    public float dst2 (Vec2 v) {
        final float x_d = v.x - x;
        final float y_d = v.y - y;
        return x_d * x_d + y_d * y_d;
    }

    /** @param x The x-component of the other vector
     * @param y The y-component of the other vector
     * @return the squared distance between this and the other vector */
    public float dst2 (float x, float y) {
        final float x_d = x - this.x;
        final float y_d = y - this.y;
        return x_d * x_d + y_d * y_d;
    }

    
    public Vec2 limit (float limit) {
        return limit2(limit * limit);
    }

    
    public Vec2 limit2 (float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            return scl((float)Math.sqrt(limit2 / len2));
        }
        return this;
    }

    
    public Vec2 clamp (float min, float max) {
        final float len2 = len2();
        if (len2 == 0f) return this;
        float max2 = max * max;
        if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
        return this;
    }

    
    public Vec2 setLength (float len) {
        return setLength2(len * len);
    }

    
    public Vec2 setLength2 (float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
    }

    /** Converts this {@code Vec2} to a string in the format {@code (x,y)}.
     * @return a string representation of this object. */
    
    public String toString () {
        return "(" + x + "," + y + ")";
    }


    /** Calculates the 2D cross product between this and the given vector.
     * @param v the other vector
     * @return the cross product */
    public float crs (Vec2 v) {
        return this.x * v.y - this.y * v.x;
    }

    /** Calculates the 2D cross product between this and the given vector.
     * @param x the x-coordinate of the other vector
     * @param y the y-coordinate of the other vector
     * @return the cross product */
    public float crs (float x, float y) {
        return this.x * y - this.y * x;
    }

    /** @return the angle in degrees of this vector (point) relative to the x-axis. Angles are towards the positive y-axis
     *         (typically counter-clockwise) and between 0 and 360. */
    public float angle () {
        float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
        if (angle < 0) angle += 360;
        return angle;
    }

    /** @return the angle in degrees of this vector (point) relative to the given vector. Angles are towards the positive y-axis
     *         (typically counter-clockwise.) between -180 and +180 */
    public float angle (Vec2 reference) {
        return (float)Math.atan2(crs(reference), dot(reference)) * MathUtils.radiansToDegrees;
    }

    /** @return the angle in radians of this vector (point) relative to the x-axis. Angles are towards the positive y-axis.
     *         (typically counter-clockwise) */
    public float angleRad () {
        return (float)Math.atan2(y, x);
    }

    /** @return the angle in radians of this vector (point) relative to the given vector. Angles are towards the positive y-axis.
     *         (typically counter-clockwise.) */
    public float angleRad (Vec2 reference) {
        return (float)Math.atan2(crs(reference), dot(reference));
    }

    /** Sets the angle of the vector in degrees relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
     * @param degrees The angle in degrees to set. */
    public Vec2 setAngle (float degrees) {
        return setAngleRad(degrees * MathUtils.degreesToRadians);
    }

    /** Sets the angle of the vector in radians relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
     * @param radians The angle in radians to set. */
    public Vec2 setAngleRad (float radians) {
        this.set(len(), 0f);
        this.rotateRad(radians);

        return this;
    }

    /** Rotates the Vec2 by the given angle, counter-clockwise assuming the y-axis points up.
     * @param degrees the angle in degrees */
    public Vec2 rotateDeg(float degrees) {
        return rotateRad(degrees * MathUtils.degreesToRadians);
    }

    /** Rotates the Vec2 by the given angle around reference vector, counter-clockwise assuming the y-axis points up.
     * @param degrees the angle in degrees
     * @param reference center Vec2 */
    public Vec2 rotateAround (Vec2 reference, float degrees) {
        return this.sub(reference).rotateDeg(degrees).add(reference);
    }

    /** Rotates the Vec2 by the given angle, counter-clockwise assuming the y-axis points up.
     * @param radians the angle in radians */
    public Vec2 rotateRad (float radians) {
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);

        float newX = this.x * cos - this.y * sin;
        float newY = this.x * sin + this.y * cos;

        this.x = newX;
        this.y = newY;

        return this;
    }

    /** Rotates the Vec2 by the given angle around reference vector, counter-clockwise assuming the y-axis points up.
     * @param radians the angle in radians
     * @param reference center Vec2 */
    public Vec2 rotateAroundRad (Vec2 reference, float radians) {
        return this.sub(reference).rotateRad(radians).add(reference);
    }

    /** Rotates the Vec2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
    public Vec2 rotate90 (int dir) {
        float x = this.x;
        if (dir >= 0) {
            this.x = -y;
            y = x;
        } else {
            this.x = y;
            y = -x;
        }
        return this;
    }

    
    public Vec2 lerp (Vec2 target, float alpha) {
        final float invAlpha = 1.0f - alpha;
        this.x = (x * invAlpha) + (target.x * alpha);
        this.y = (y * invAlpha) + (target.y * alpha);
        return this;
    }




    
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vec2 other = (Vec2)obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    
    public boolean epsilonEquals (Vec2 other, float epsilon) {
        if (other == null) return false;
        if (Math.abs(other.x - x) > epsilon) return false;
        if (Math.abs(other.y - y) > epsilon) return false;
        return true;
    }

    /** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
     * @return whether the vectors are the same. */
    public boolean epsilonEquals (float x, float y, float epsilon) {
        if (Math.abs(x - this.x) > epsilon) return false;
        if (Math.abs(y - this.y) > epsilon) return false;
        return true;
    }

}
