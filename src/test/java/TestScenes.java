import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.math.MathUtils;
import com.chalq.object2d.graph.GraphPlotter;
import com.chalq.object2d.shape2d.Rectangle;
import com.chalq.util.Color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.Random;

import static com.chalq.core.Cq.*;
import static com.chalq.core.Cq.time;

public class TestScenes {

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        config.antialiasing = true;
//        config.outputMP4Path = "vidout/thousandsquares.mp4";
        new CqWindow(config, new Scene2());

//        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
//        byte[] imageData = ( (DataBufferByte) image.getRaster().getDataBuffer() ).getData();
//        System.out.println(imageData.length);
//        ByteBuffer buffer = ByteBuffer.allocateDirect(1920 * 1080 * 3);
//        System.out.println(1920 * 1080 * 3);
//        System.out.println("buffer size: " + buffer.remaining());
    }

    static class Scene1 extends CqScene {

        GraphPlotter plotter;

        @Override
        public void init() {
            plotter = new GraphPlotter(200, 200, 800, 400, -10, 10, -5, 5);
            plotter.addFunction(this::f, -10, 10, new Color(1, 0, 0, 1), true);
            plotter.addFunction(this::g, -10, 10, new Color("#56B0FF"), true);
            traceObject(plotter, 0.5f);
        }

        @Override
        public void update() {
        }

        public float f(float x) {
            return x * x * x;
        }

        public float g(float x) {
            return (float)( Math.sin(x - Cq.time) + Math.cos(x * 0.7 + Cq.time)) * 5;
        }

    }

    static class Scene2 extends CqScene {

        Rectangle[] rects = new Rectangle[1000];

        @Override
        public void init() {
            Easing easing = Easing.EASE_IN_OUT;
            {
                GraphPlotter p1 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p1.addFunction(this::w1c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p1.setAxesVisible(false);
                addChild(p1);
                interpolate(p1::setY, p1.getY(), p1.getY() - 350, time + 3, 1.5f, easing);
            }
            {
                GraphPlotter p2 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p2.addFunction(this::w2c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p2.setAxesVisible(false);
                addChild(p2);
                interpolate(p2::setY, p2.getY(), p2.getY() - 350 / 3f, time + 3, 1.5f, easing);
            }
            {
                GraphPlotter p3 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p3.addFunction(this::w3c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p3.setAxesVisible(false);
                addChild(p3);
                interpolate(p3::setY, p3.getY(), p3.getY() + 350 / 3f, time + 3, 1.5f, easing);
            }
            {
                GraphPlotter p4 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p4.addFunction(this::w4c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p4.setAxesVisible(false);
                addChild(p4);
                interpolate(p4::setY, p4.getY(), p4.getY() + 350, time + 3, 1.5f, easing);
            }
            interpolate(this::setMorphFac, 0, 1, time + 3, 1.5f, easing);

            Random random = new Random();
            for (int i = 0; i < rects.length; i++) {
                rects[i] = new Rectangle(random.nextInt(1920), random.nextInt(1080), 30, 30);
                addChild(rects[i]);
            }
        }

        @Override
        public void update() {
            for (Rectangle r : rects) {
                r.setRotation(time);
            }
        }

        float morphFac = 0;
        private void setMorphFac(float morphFac) { this.morphFac = morphFac; }

        private float w1(float in) { return (float) Math.sin((in - time * 1.5) * 1 ) * 0.7f; }
        private float w2(float in) { return (float) Math.sin((in - time * 1.5) * 2 ) * 0.7f; }
        private float w3(float in) { return (float) Math.sin((in - time * 1.5) * 3 ) * 0.7f; }
        private float w4(float in) { return (float) Math.sin((in - time * 1.5) * 4 ) * 0.7f; }

        private float wsum(float in) { return w1(in) + w2(in) + w3(in) + w4(in); }

        private float w1c(float in) { return MathUtils.lerp( wsum(in), w1(in), morphFac); }
        private float w2c(float in) { return MathUtils.lerp( wsum(in), w2(in), morphFac); }
        private float w3c(float in) { return MathUtils.lerp( wsum(in), w3(in), morphFac); }
        private float w4c(float in) { return MathUtils.lerp( wsum(in), w4(in), morphFac); }
    }

}
