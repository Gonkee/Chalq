package com.chalq.util;

public class MathUtils {

    public static short clamp (short value, short min, short max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static int clamp (int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static long clamp (long value, long min, long max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float clamp (float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double clamp (double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static float lerp (float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    public static double lerp (double fromValue, double toValue, double progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    public static float smoothStep (float fromValue, float toValue, float progress) {
        progress = clamp(progress, 0, 1);
        progress = progress * progress * (3 - 2 * progress);
        return fromValue + (toValue - fromValue) * progress;
    }

    public static double smoothStep (double fromValue, double toValue, double progress) {
        progress = clamp(progress, 0, 1);
        progress = progress * progress * (3 - 2 * progress);
        return fromValue + (toValue - fromValue) * progress;
    }

    public static int floor(float f) {
        return f >= 0 ? (int) f : (int) f - 1;
    }

    public static float fract(float f) {
        return f - floor(f);
    }

    public static float roundPlaces(float value, int decimalPlaces) {
        return Math.round(value * Math.pow(10, decimalPlaces)) / (float) Math.pow(10, decimalPlaces);
    }

    public static double roundPlaces(double value, int decimalPlaces) {
        return Math.round(value * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

}
