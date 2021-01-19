
package com.chalq.util;


public class Color {

    public static final Color WHITE = new Color(1, 1, 1, 1);

    public final float r, g, b, a;

    public Color (float r, float g, float b, float a) {
        this.r = MathUtils.clamp(r, 0, 1);
        this.g = MathUtils.clamp(g, 0, 1);
        this.b = MathUtils.clamp(b, 0, 1);
        this.a = MathUtils.clamp(a, 0, 1);
    }

    /** Returns a new color from a hex string with the format RRGGBBAA.
     * @see #toString() */
    public Color (String hex) {
        hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
        r = Integer.valueOf(hex.substring(0, 2), 16) / 255f;
        g = Integer.valueOf(hex.substring(2, 4), 16) / 255f;
        b = Integer.valueOf(hex.substring(4, 6), 16) / 255f;
        a = hex.length() != 8 ? 1 : Integer.valueOf(hex.substring(6, 8), 16) / 255f;
    }

    public static Color lerp(Color fromColor, Color toColor, float fac) {
        float r = MathUtils.lerp(fromColor.r, toColor.r, fac);
        float g = MathUtils.lerp(fromColor.g, toColor.g, fac);
        float b = MathUtils.lerp(fromColor.b, toColor.b, fac);
        float a = MathUtils.lerp(fromColor.a, toColor.a, fac);
        return new Color(r, g, b, a);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b && a == color.a;
    }

    public int getIntRGBA () {
        return ((int)(255 * r) << 24) | ((int)(255 * g) << 16) | ((int)(255 * b) << 8) | ((int)(255 * a));
    }




}
