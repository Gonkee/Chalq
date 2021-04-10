import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.math.LerpVal;
import com.chalq.math.MathUtils;
import com.chalq.object2d.Arrow;
import com.chalq.object2d.graph.GraphPlotter;
import com.chalq.math.Vec2;
import com.chalq.object2d.shape2d.Rectangle;
import com.chalq.util.Color;


import static com.chalq.core.Cq.*;

public class SoundWave extends CqScene {


    float width = 800;
    float height = 200;

    GraphPlotter wave;
    Rectangle rect1;
    Arrow arc;

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        config.antialiasing = true;
        new CqWindow(config, new SoundWave());
    }

    @Override
    public void init() {
//        System.out.println("divide by 0");
//        System.out.println();
//        System.out.println(1 / 0);
//        wave = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f + 20, getFrameHeight() / 2f - 400 / 2f, 700, 400,
//                0, 30, 0, 30);
//        wave.addFunction(GraphPlotter.toParametric(this::wave, false), 1, 29.9f, new Color("#eb4034"), true);
//
//
//        traceObject(wave, 2);

        GraphPlotter plotter = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 3, 0, 10);
//        plotter.addFunction(GraphPlotter.toParametric(this::w1, false), 0, 4 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::w2, false), 0, 4 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::w3, false), 0, 4 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::w4, false), 0, 4 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::w5, false), 0, 4 * (float) Math.PI, Color.WHITE, true);

//        plotter.addFunction(GraphPlotter.toParametric(this::sine, false), 0, 6 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::triangle, false), 0, 6 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::square, false), 0, 6 * (float) Math.PI, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::saw, false), 0, 6 * (float) Math.PI, Color.WHITE, true);
        plotter.addFunction(this::sawMorph, 0, 3, Color.WHITE, true);
//        plotter.addFunction(GraphPlotter.toParametric(this::wave, false), 0, 2 * (float) Math.PI, Color.WHITE, true);

        plotter.setAxesVisible(false);
//        popUpObjectSlow(plotter, 1, 0.5f);
        addChild(plotter);
        lerpCustom(hm, 16, time + 4, 8);



//        GraphPlotter circleGraph = new GraphPlotter(getFrameWidth() / 2f - width / 2 - 20, getFrameHeight() / 2f - height / 2 - 150, width, width,
//                -2, 2, -2, 2);
//        circleGraph.addFunction(this::circleFunc, 0, 400, Color.WHITE, true);
//
//        popUpObjectSlow(circleGraph, 1, 500, 200);
//        interpolate(wave.pos, new Vec2(200, 200), time + 2, 2);
//
//        ArcArrow arrow = new ArcArrow(300, 150, 100, 20, 120, 5);
//        popUpObjectSlow(arrow, 1, 300, 150);

        // many layers hierarchy seems to cause stutters
//        rect1 = new Rectangle(500, 300, 200, 200);
//        Rectangle rect2 = new Rectangle(50, 80, 70, 70);
//        Rectangle rect3 = new Rectangle(70, 0, 30, 30);
//        Rectangle rect4 = new Rectangle(0, 100, 10, 10);
//        rect1.addChild(rect2);
//        rect2.addChild(rect3);
//        rect3.addChild(rect4);

//        traceObject(rect1, 0.5f);
//        addChild(rect1);
//        rect1.setPos(900, 500);
//        rect1.setOffset(-50, -80);
//
//
//        arc = new ArcArrow(600, 300, 100, 0, 270, 5);
//        traceObject(arc, 1);

//        Arrow a1 = new Arrow(960 - 300, 540 - 50, 270, 0, 0, 6);
//        Arrow a2 = new Arrow(960 + 30, 540 - 50, 270, 0, 0, 6);
//        traceObject(a1, 2);
//        traceObject(a2, 3.5f);
//        Random random = new Random();
//        for (int i = 0; i < 1000; i++) {
//            addChild(new Rectangle(100 + random.nextInt(1500), 100 + random.nextInt(800), 30,30));
//        }
//        rect.addAndTrace(this, 500, 300);
//        addDrawable(rect);
//        interpolate(rect.scale, new Vec2(0.5f, 0.5f), time + 1.5f, 1);
//        interpolate(rect.scale, new Vec2(5f, 5f), time + 2.5f, 1);
//        interpolate(rect.outline.traceProgress, 1, time + 1.5f, 2);

//        addDrawable(new Line(100, 600, 0, 300));

//        Circle circle = new Circle(600, 800, 50);
//        addDrawable(circle);
//        interpolate(circle.outline.traceProgress, 1, time + 1.5f, 2);
    }


    @Override
    public void update() {

        if (frameTime > 0.01) System.out.println("stutter: " + frameTime + " sec");

//        rect1.setPos(500 + (float) Math.sin(time) * 300, 500);
//
//        float scl = (float) Math.sin(time) / 4 + 0.75f;
//        rect1.setScale(scl, scl);
//        rect1.setRotation((float)Math.cos(time) * 2);

//        Cq.textSettings(80, TextAlignH.CENTER, TextAlignV.CENTER);
//        Cq.setColor(Color.WHITE);
//        Cq.text("200Hz", 960 - 300, 540);
//        Cq.text("400Hz", 960, 540);
//        Cq.text("800Hz", 960 + 300, 540);

//        arc.setTraceProgress((float) Math.sin(time) / 4 + 0.75f);
//        Cq.arcClockwise(500, 500, 600, 600, 180, 5);

//        int columns = 20;
//        int rows = 10;
//
//        float dotsPerPeriod = 10;
//        for (int i = 0; i < columns; i++) {
//            float offsetX = (float) Math.sin(i / dotsPerPeriod * 2 * Math.PI - time * 3) * width / columns * 0.8f;
//            for (int g = 0; g < rows; g++) {
//                float dotX = (getFrameWidth() / 2f - width / 2) + width * ((float) i / columns) + offsetX;
//                float dotY = (getFrameHeight() / 2f - height / 2) + height * ((float) g / rows) - 80;
//                fillCircle(dotX, dotY, 5);
//            }
//        }
//
//        Cq.arcClockwise(1500, 600, 1500, 500, -270, 4);
//        Cq.setColor(1, 0, 0, 1);
//        Cq.fillCircle(1500, 600, 10);
//        Cq.fillCircle(1500, 500, 10);


    }

    private float wave(float in) {


        return (float) Math.sin(in * 3.5 - time * 3) * 2 + 5;
//        return (float) Math.pow(1.12, in);
    }

    private float sine(float in) {
        return (float) Math.sin(in - time * 3) + 20;
    }
    private float triangle(float in) {
        float pi2 = (float) Math.PI * 2;
        in -= time * 3;
        in %= pi2;
        if (in < 0) in += pi2;
        in = in > pi2/2 ? pi2 - in : in;
        in /= pi2/2;
        in *= 2;
        in -= 1;
        return in + 15;
    }
    private float square(float in) {
        float pi2 = (float) Math.PI * 2;
        in -= time * 3;
        in %= pi2;
        if (in < 0) in += pi2;
        in = in > pi2/2 ? 1 : -1;
        return in + 10;
    }
    private float saw(float in) {
        float pi2 = (float) Math.PI * 2;
        in -= time * 3;
        in %= pi2;
        if (in < 0) in += pi2;
        in = in / pi2 * 2 - 1;
        return in + 5;
    }

    private Vec2 circleFunc(float t) {
        return new Vec2(0, 1).rotateDeg(t);
    }

    private float f1(float in) { return (float) Math.sin(in * 4) * 0.7f + 14; }
    private float f2(float in) { return (float) Math.sin(in * 5) * 0.7f + 7; }
    private float fs(float in) { return (float) (Math.sin(in * 4) * 0.7f + Math.sin(in * 5) * 0.7f); }

    LerpVal morphFac = new LerpVal();

    private float w1(float in) { return (float) Math.sin((in - time * 1.5) * 1 ) * 0.7f; }
    private float w2(float in) { return (float) Math.sin((in - time * 1.5) * 2 ) * 0.7f; }
    private float w3(float in) { return (float) Math.sin((in - time * 1.5) * 3 ) * 0.7f; }
    private float w4(float in) { return (float) Math.sin((in - time * 1.5) * 4 ) * 0.7f; }
    private float w5(float in) { return (float) Math.sin((in - time * 1.5) * 5 ) * 0.7f; }

    private float wsum(float in) {
        return w1(in) + w2(in) + w3(in) + w4(in);
    }

    private float w1c(float in) { return MathUtils.lerp( wsum(in), w1(in), morphFac.val); }
    private float w2c(float in) { return MathUtils.lerp( wsum(in), w2(in), morphFac.val); }
    private float w3c(float in) { return MathUtils.lerp( wsum(in), w3(in), morphFac.val); }
    private float w4c(float in) { return MathUtils.lerp( wsum(in), w4(in), morphFac.val); }

    LerpVal hm = new LerpVal(1);

    private float harmonic(float in) {
        float h1 = (float) Math.sin(in);
        float h2 = (float) Math.sin(in * 1.4142);
        float h3 = (float) Math.sin(in * 2.1415);
        float h4 = (float) Math.sin(in * 4.7878);
        float h5 = (float) Math.sin(in * 6.656);

        float result =
                h1 * MathUtils.clamp(hm.val    , 0, 1) +
                h2 * MathUtils.clamp(hm.val - 3, 0, 1) +
                h3 * MathUtils.clamp(hm.val - 6, 0, 1) +
                h4 * MathUtils.clamp(hm.val - 9, 0, 1) +
                h5 * MathUtils.clamp(hm.val - 12, 0, 1) + 10;

        return result;
    }

    private float squareMorph(float in) {

        float sum = 0;
        for (int k = 1; k <= (int) hm.val + 1; k++) {
            double next = Math.sin(2 * Math.PI * (2 * k - 1) * in) / (2 * k - 1);
            if (k < (int) hm.val + 1) {
                sum += next;
            } else {
                sum += next * MathUtils.clamp(3 * (hm.val - (int) hm.val), 0, 1);
            }
        }

        return sum * 4 / (float) Math.PI + 5;
    }
    private float triangleMorph(float in) {

        float sum = 0;
        for (int k = 1; k <= (int) hm.val + 1; k++) {
            double next = Math.pow(-1, k) * Math.sin(2 * Math.PI * (2 * k - 1) * in) /  ( (2 * k - 1) * (2 * k - 1) );
            if (k < (int) hm.val + 1) {
                sum += next;
            } else {
                sum += next * MathUtils.clamp(3 * (hm.val - (int) hm.val), 0, 1);
            }
        }

        return sum * 8 / (float) (Math.PI * Math.PI) + 5;
    }

    private float sawMorph(float in) {

        float sum = 0;
        for (int k = 1; k <= (int) hm.val + 1; k++) {
            double next = Math.pow(-1, k) * Math.sin(2 * Math.PI * k * in) / k;
            if (k < (int) hm.val + 1) {
                sum += next;
            } else {
                sum += next * MathUtils.clamp(3 * (hm.val - (int) hm.val), 0, 1);
            }
        }

        return sum * 2 / (float) (Math.PI) + 5;
    }

}
