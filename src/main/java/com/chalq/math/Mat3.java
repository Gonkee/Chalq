package com.chalq.math;

public class Mat3 {

    // element naming format: mXY
    public float m00, m01, m02, m10, m11, m12, m20, m21, m22;

    public static Mat3 add(Mat3 left, Mat3 right, Mat3 dest) {
        if (dest == null) dest = new Mat3();
        dest.m00 = left.m00 + right.m00;
        dest.m01 = left.m01 + right.m01;
        dest.m02 = left.m02 + right.m02;
        dest.m10 = left.m10 + right.m10;
        dest.m11 = left.m11 + right.m11;
        dest.m12 = left.m12 + right.m12;
        dest.m20 = left.m20 + right.m20;
        dest.m21 = left.m21 + right.m21;
        dest.m22 = left.m22 + right.m22;
        return dest;
    }

    public static Mat3 subtract(Mat3 left, Mat3 right, Mat3 dest) {
        if (dest == null) dest = new Mat3();
        dest.m00 = left.m00 - right.m00;
        dest.m01 = left.m01 - right.m01;
        dest.m02 = left.m02 - right.m02;
        dest.m10 = left.m10 - right.m10;
        dest.m11 = left.m11 - right.m11;
        dest.m12 = left.m12 - right.m12;
        dest.m20 = left.m20 - right.m20;
        dest.m21 = left.m21 - right.m21;
        dest.m22 = left.m22 - right.m22;
        return dest;
    }

    public static Mat3 scale(Mat3 src, float scl, Mat3 dest) {
        dest.m00 = src.m00 * scl;
        dest.m01 = src.m01 * scl;
        dest.m02 = src.m02 * scl;
        dest.m10 = src.m10 * scl;
        dest.m11 = src.m11 * scl;
        dest.m12 = src.m12 * scl;
        dest.m20 = src.m20 * scl;
        dest.m21 = src.m21 * scl;
        dest.m22 = src.m22 * scl;
        return dest;
    }

    public static Mat3 multiply(Mat3 left, Mat3 right, Mat3 dest) {
        if (dest == null) dest = new Mat3();

        // temp values, in case dest == left or dest == right
        float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02;
        float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02;
        float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02;
        float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12;
        float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12;
        float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12;
        float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22;
        float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22;
        float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22;

        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;

        return dest;
    }

    public static Mat3 transpose(Mat3 src, Mat3 dest) {
        if (dest == null) dest = new Mat3();

        // in case dest == src
        float m00 = src.m00;
        float m01 = src.m10;
        float m02 = src.m20;
        float m10 = src.m01;
        float m11 = src.m11;
        float m12 = src.m21;
        float m20 = src.m02;
        float m21 = src.m12;
        float m22 = src.m22;

        dest.m00 = m00;
        dest.m01 = m01;
        dest.m02 = m02;
        dest.m10 = m10;
        dest.m11 = m11;
        dest.m12 = m12;
        dest.m20 = m20;
        dest.m21 = m21;
        dest.m22 = m22;
        return dest;
    }

    public static Mat3 setIdentity(Mat3 m) {
        m.m00 = 1.0f;
        m.m01 = 0.0f;
        m.m02 = 0.0f;
        m.m10 = 0.0f;
        m.m11 = 1.0f;
        m.m12 = 0.0f;
        m.m20 = 0.0f;
        m.m21 = 0.0f;
        m.m22 = 1.0f;
        return m;
    }

    public static Mat3 setZero(Mat3 m) {
        m.m00 = 0.0f;
        m.m01 = 0.0f;
        m.m02 = 0.0f;
        m.m10 = 0.0f;
        m.m11 = 0.0f;
        m.m12 = 0.0f;
        m.m20 = 0.0f;
        m.m21 = 0.0f;
        m.m22 = 0.0f;
        return m;
    }

    public static Mat3 negate(Mat3 src, Mat3 dest) {
        if (dest == null)
            dest = new Mat3();

        dest.m00 = -src.m00;
        dest.m01 = -src.m02;
        dest.m02 = -src.m01;
        dest.m10 = -src.m10;
        dest.m11 = -src.m12;
        dest.m12 = -src.m11;
        dest.m20 = -src.m20;
        dest.m21 = -src.m22;
        dest.m22 = -src.m21;
        return dest;
    }

    public static Mat3 invert(Mat3 src, Mat3 dest) {
        float determinant = src.determinant();

        if (determinant != 0) {
            if (dest == null)
                dest = new Mat3();
            /* do it the ordinary way
             *
             * inv(A) = 1/det(A) * adj(T), where adj(T) = transpose(Conjugate Matrix)
             *
             * m00 m01 m02
             * m10 m11 m12
             * m20 m21 m22
             */
            float determinant_inv = 1f/determinant;

            // get the conjugate matrix
            float t00 = src.m11 * src.m22 - src.m12* src.m21;
            float t01 = - src.m10 * src.m22 + src.m12 * src.m20;
            float t02 = src.m10 * src.m21 - src.m11 * src.m20;
            float t10 = - src.m01 * src.m22 + src.m02 * src.m21;
            float t11 = src.m00 * src.m22 - src.m02 * src.m20;
            float t12 = - src.m00 * src.m21 + src.m01 * src.m20;
            float t20 = src.m01 * src.m12 - src.m02 * src.m11;
            float t21 = -src.m00 * src.m12 + src.m02 * src.m10;
            float t22 = src.m00 * src.m11 - src.m01 * src.m10;

            dest.m00 = t00*determinant_inv;
            dest.m11 = t11*determinant_inv;
            dest.m22 = t22*determinant_inv;
            dest.m01 = t10*determinant_inv;
            dest.m10 = t01*determinant_inv;
            dest.m20 = t02*determinant_inv;
            dest.m02 = t20*determinant_inv;
            dest.m12 = t21*determinant_inv;
            dest.m21 = t12*determinant_inv;
            return dest;
        } else
            return null;
    }

    public Mat3() {
        m00 = 1;
        m11 = 1;
        m22 = 1;
    }
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(m00).append(' ').append(m10).append(' ').append(m20).append(' ').append('\n');
        buf.append(m01).append(' ').append(m11).append(' ').append(m21).append(' ').append('\n');
        buf.append(m02).append(' ').append(m12).append(' ').append(m22).append(' ').append('\n');
        return buf.toString();
    }

    public Mat3 set(Mat3 m) {
        m00 = m.m00;
        m01 = m.m01;
        m02 = m.m02;
        m10 = m.m10;
        m11 = m.m11;
        m12 = m.m12;
        m20 = m.m20;
        m21 = m.m21;
        m22 = m.m22;
        return this;
    }

    public Mat3 add(Mat3 mat3) {
        return add(this, mat3, this);
    }

    public Mat3 subtract(Mat3 mat3) {
        return subtract(this, mat3, this);
    }

    public Mat3 scale(float scl) {
        return scale(this, scl, this);
    }

    public Mat3 multiply(Mat3 mat3) {
        return multiply(this, mat3, this);
    }

    public Mat3 transpose() {
        return transpose(this, this);
    }

    public Mat3 setIdentity() {
        return setIdentity(this);
    }

    public Mat3 setZero() {
        return setZero(this);
    }

    public Mat3 negate() {
        return negate(this, this);
    }

    public Mat3 invert() {
        return invert(this, this);
    }

    public float determinant() {
        return m00 * (m11 * m22 - m12 * m21)
                + m01 * (m12 * m20 - m10 * m22)
                + m02 * (m10 * m21 - m11 * m20);
    }
}
