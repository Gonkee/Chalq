package com.chalq.util;

public class MathUtils {

    static public short clamp (short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public int clamp (int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public long clamp (long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public float clamp (float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public double clamp (double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    static public float lerp (float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    static public double lerp (double fromValue, double toValue, double progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    public static int floor(float f) {
        return f >= 0 ? (int) f : (int) f - 1;
    }

    public static float fract(float f) {
        return f - floor(f);
    }
}
