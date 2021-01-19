package com.chalq.core;


public class Graphics {

//    private final long nvg;
//
//    protected Graphics(long nvg) {
//        this.nvg = nvg;
//    }
//
//    public final Canvas canvas;
//    private int bgColor;
//
//    private Typeface typeface = Typeface.makeFromFile("fonts/bahnschrift.ttf");
//    private Font font = new Font(typeface, 40);
//    private Shaper shaper = Shaper.make();
//    private Paint paint = new Paint().setColor(Color.makeRGB(255, 255, 255));
//
//    public Graphics(Canvas canvas, float bgR, float bgG, float bgB) {
//        this.canvas = canvas;
//        bgColor = Color.makeRGB( (int) (bgR * 255), (int) (bgG * 255), (int) (bgB * 255) );
//        paint.setAntiAlias(false);
//    }
//
//    public void clearBG() {
//        canvas.clear(bgColor);
//    }
//
//    public void text(String s, float x, float y) {
//        TextBlob blob = shaper.shape(s, font);
//        if (blob != null) canvas.drawTextBlob(blob, x, y, paint);
//    }
//
//    public void line(float x1, float y1, float x2, float y2, float width) {
//        paint.setStrokeWidth(width);
//        canvas.drawLine(x1, y1, x2, y2, paint);
//    }
//
//    public void rectStroke(float x, float y, float width, float height, float strokeWidth) {
//        float halfStroke = strokeWidth / 2;
//        line(x - halfStroke, y, x + width + halfStroke, y, strokeWidth);
//        line(x, y - halfStroke, x, y + height + halfStroke, strokeWidth);
//        line(x + width, y - halfStroke, x + width, y + height + halfStroke, strokeWidth);
//        line(x - halfStroke, y + height, x + width + halfStroke, y + height, strokeWidth);
//    }
//
//    public void setColor(com.inspectolib.math.Color color) {
//        paint.setARGB((int) (color.a * 255), (int) (color.r * 255), (int) (color.g * 255), (int) (color.b * 255));
////        paint.setColor(color.getIntRGBA());
//    }
//
//    public void setColor(float r, float g, float b, float a) {
//        setColor(new com.inspectolib.math.Color(r, g, b, a));
//    }
//
//    public void circle(float x, float y, float radius) {
//        canvas.drawCircle(x, y, radius, paint);
//    }

}
