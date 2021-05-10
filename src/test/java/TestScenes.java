import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.math.MathUtils;
import com.chalq.object2d.FunctionGraph;
import com.chalq.object2d.GraphSpace;
import com.chalq.object2d.Particles;
import com.chalq.object2d.VectorField;
import com.chalq.object2d.shape2d.Rectangle;
import com.chalq.util.Color;

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
//        config.outputMP4Path = "vidout/loryerr.mp4";
        new CqWindow(config, new GraphSpaceScene());

    }

    static class Scene1 extends CqScene {

        GraphSpace plotter;
        Particles particles;
        Random rand = new Random();

        int numPoints = 200;
        float[] points = new float[numPoints * 3];

        @Override
        public void init() {
            plotter = new GraphSpace(200, 200, 1520, 680, -15, 20, -30, 30);

            FunctionGraph fg1 = new FunctionGraph(this::f, -10, 10, new Color(1, 0, 0, 1), 300, true, false);
            FunctionGraph fg2 = new FunctionGraph(this::g, -10, 10, new Color("#56B0FF"), 300, true, false);
//            plotter.addToGraphSpace(fg1);
//            plotter.addToGraphSpace(fg2);
            addChild(plotter);

            particles = new Particles(2, 20, 1, 2, new Color("#db1247"));
            for (int i = 0; i < numPoints; i++) {

                points[i * 3    ] = -15 + rand.nextFloat() * 35;
                points[i * 3 + 1] = -30 + rand.nextFloat() * 60;
                points[i * 3 + 2] = rand.nextFloat() * 50;

                particles.addParticle(points[i * 3    ], points[i * 3 + 1]);
            }
            plotter.addToGraphSpace(particles);

        }

        @Override
        public void update() {
//            float dx = (float) Math.cos(time * 4) + (float) Math.cos(time);
//            float dy = (float) Math.sin(time * 4);
//            dx *= 0.1;
//            dy *= 0.1;


            float alpha = 0.1f;
            float beta = 0.1f;
            float gamma = 0.1f;
            float delta2 = 0.1f;
            float x, y, z, dx, dy, dz;

            float a = -0.64f;
            float b = 0.76f;

            for (int i = 0; i < numPoints; i++) {


//                x = particles.getX(i);
//                y = particles.getY(i);
//                dx = 10 * delta * x * (alpha - beta * y);
//                dy = 10 * delta * y * (delta2 * x - gamma);

                x = points[i * 3    ];
                y = points[i * 3 + 1];
                z = points[i * 3 + 2];

//                dx = 3.0f * (1 - x) - 2.2f * z;
//                dy = -1.0f * (1 - x * x) * y;
//                dz = 0.001f * x;

                dx = 10 * (y - x);
                dy = x * (28 - z) - y;
                dz = x * y - (8f / 3f) * z;

                dx *= 0.5f;
                dy *= 0.5f;
                dz *= 0.5f;

                x += dx * delta;
                y += dy * delta;
                z += dz * delta;

                points[i * 3    ] = x;
                points[i * 3 + 1] = y;
                points[i * 3 + 2] = z;


//                float xnew= (float) (Math.sin(x * y / b) * y + Math.cos(a * x - y));
//                float ynew= (float) (x + Math.sin(y) / b);

                particles.updateParticle(i, x, y);
//                particles.updateParticle(i, x + dx, y + dy);
            }
        }

        public float f(float x) {
            return x * x * x;
        }

        public float g(float x) {
            return (float)( Math.sin(x - Cq.time) + Math.cos(x * 0.7 + Cq.time)) * 5;
        }

    }

//    static class Scene2 extends CqScene {
//
//        Rectangle[] rects = new Rectangle[1000];
//
//        @Override
//        public void init() {
//            Easing easing = Easing.EASE_IN_OUT;
//            {
//                GraphPlotter p1 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
//                p1.addFunction(this::w1c, 0, 2 * (float) Math.PI, Color.WHITE, true);
//                p1.setAxesVisible(false);
//                addChild(p1);
//                interpolate(p1::setY, p1.getY(), p1.getY() - 350, time + 3, 1.5f, easing);
//            }
//            {
//                GraphPlotter p2 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
//                p2.addFunction(this::w2c, 0, 2 * (float) Math.PI, Color.WHITE, true);
//                p2.setAxesVisible(false);
//                addChild(p2);
//                interpolate(p2::setY, p2.getY(), p2.getY() - 350 / 3f, time + 3, 1.5f, easing);
//            }
//            {
//                GraphPlotter p3 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
//                p3.addFunction(this::w3c, 0, 2 * (float) Math.PI, Color.WHITE, true);
//                p3.setAxesVisible(false);
//                addChild(p3);
//                interpolate(p3::setY, p3.getY(), p3.getY() + 350 / 3f, time + 3, 1.5f, easing);
//            }
//            {
//                GraphPlotter p4 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
//                p4.addFunction(this::w4c, 0, 2 * (float) Math.PI, Color.WHITE, true);
//                p4.setAxesVisible(false);
//                addChild(p4);
//                interpolate(p4::setY, p4.getY(), p4.getY() + 350, time + 3, 1.5f, easing);
//            }
//            interpolate(this::setMorphFac, 0, 1, time + 3, 1.5f, easing);
//
//            Random random = new Random();
//            for (int i = 0; i < rects.length; i++) {
//                rects[i] = new Rectangle(random.nextInt(1920), random.nextInt(1080), 30, 30);
//                addChild(rects[i]);
//            }
//        }
//
//        @Override
//        public void update() {
//            for (Rectangle r : rects) {
//                r.setRotation(time);
//            }
//        }
//
//        float morphFac = 0;
//        private void setMorphFac(float morphFac) { this.morphFac = morphFac; }
//
//        private float w1(float in) { return (float) Math.sin((in - time * 1.5) * 1 ) * 0.7f; }
//        private float w2(float in) { return (float) Math.sin((in - time * 1.5) * 2 ) * 0.7f; }
//        private float w3(float in) { return (float) Math.sin((in - time * 1.5) * 3 ) * 0.7f; }
//        private float w4(float in) { return (float) Math.sin((in - time * 1.5) * 4 ) * 0.7f; }
//
//        private float wsum(float in) { return w1(in) + w2(in) + w3(in) + w4(in); }
//
//        private float w1c(float in) { return MathUtils.lerp( wsum(in), w1(in), morphFac); }
//        private float w2c(float in) { return MathUtils.lerp( wsum(in), w2(in), morphFac); }
//        private float w3c(float in) { return MathUtils.lerp( wsum(in), w3(in), morphFac); }
//        private float w4c(float in) { return MathUtils.lerp( wsum(in), w4(in), morphFac); }
//    }

    static class ParticlesScene extends CqScene {

        Particles particles = new Particles(0, 10, 1, 5, new Color("#db1247"));
        Random rand = new Random();

        @Override
        public void init() {
//            particles.addParticle(960, 540);
            for (int i = 0; i < 10; i++) {
                particles.addParticle(200 + rand.nextInt(1500), 100 + rand.nextInt(800));
            }
            addChild(particles);
        }

        @Override
        public void update() {

            float dx = (float) Math.cos(time * 4) + (float) Math.cos(time);
            float dy = (float) Math.sin(time * 4);
            dx *= 2;
            dy *= 2;

            for (int i = 0; i < particles.getSize(); i++) {
                particles.updateParticle(i, particles.getX(i) + dx, particles.getY(i) + dy);
            }
        }

    }

    static class GraphSpaceScene extends CqScene {

        GraphSpace graphSpace = new GraphSpace(200, 100, 1520, 880, -8, 8, -10, 10);
        VectorField vectorField = new VectorField(-8, 8, -10, 10, 0.6f, 15, new Color("#ff3838"), 4);

        @Override
        public void init() {
            popUpObjectSlow(graphSpace, 1, 0);
            graphSpace.addToGraphSpace(vectorField);

            float realX, realY;
            for (int x = 0; x < vectorField.xCount(); x++) {
                for (int y = 0; y < vectorField.yCount(); y++) {
                    realX = vectorField.getX(x);
                    realY = vectorField.getY(y);
                    vectorField.setVec(x, y,
                            realX - realX * realX * realX / 3 - realY,
                            realX
                    );
                }
            }
        }

        @Override
        public void update() {

//            float realX, realY;
//            for (int x = 0; x < vectorField.xCount(); x++) {
//                for (int y = 0; y < vectorField.xCount(); y++) {
//                    realX = vectorField.getX(x);
//                    realY = vectorField.getY(y);
//                    vectorField.setVec(x, y, (float) Math.cos(realX + time) * 0.5f, (float) Math.cos(realY + time) * 0.5f);
//                }
//            }
        }
    }

}
