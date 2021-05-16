import com.chalq.core.*;
import com.chalq.math.Mat4;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.math.Vec3;
import com.chalq.object2d.*;
import com.chalq.object2d.shape2d.Circle;
import com.chalq.util.Color;

import java.util.Random;

import static com.chalq.core.Cq.*;
import static com.chalq.core.Cq.time;

public class TestScenes {

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color("#1A2333");
        config.antialiasing = true;
        config.outputMP4Path = "vidout/lorenzInfiniteDetail.mp4";
        new CqWindow(config, new Scene1());

    }

    static class Scene1 extends CqScene {

        GraphSpace plotter;
        Particles particles;
        Particles particles2;
        Random rand = new Random();

        int numPoints = 10;
        float[] points = new float[numPoints * 3];
        float[] points2 = new float[3];

        float timeToStartAddingParticles;
        float addParticlesDuration = 3;

        public void addParticle(int i) {
            points[i * 3    ] = -25 + rand.nextFloat() * 50;
            points[i * 3 + 1] = -30 + rand.nextFloat() * 60;
            points[i * 3 + 2] = rand.nextFloat() * 50;

//            particles.addParticle(points[i * 3    ], points[i * 3 + 2]);
        }

        public void addParticle2(int i) {
            points2[i * 3    ] = -25 + rand.nextFloat() * 50;
            points2[i * 3 + 1] = -30 + rand.nextFloat() * 60;
            points2[i * 3 + 2] = rand.nextFloat() * 50;

            particles2.addParticle(points2[i * 3    ], points2[i * 3 + 2]);
        }


        @Override
        public void init() {
            plotter = new GraphSpace(200, 100, 1520, 880, -20, 20, 0, 45);
            addChild(plotter);

            particles = new Particles(3, 2000, 2, 2, new Color("#FFF000"));
            particles2 = new Particles(4, 2000, 3, 2, new Color("#00CC00"));
            plotter.addToGraphSpace(particles);
            plotter.addToGraphSpace(particles2);

            plotter.yMarkingDecimalPlaces = 0;
            plotter.xMarkingDecimalPlaces = 0;
            plotter.xMarkingInterval = 5;
            plotter.yMarkingInterval = 5;

            plotter.xLabel = "X";
            plotter.yLabel = "Z";

            timeToStartAddingParticles = time + 2;
            for (int i = 0; i < numPoints; i++) {
                addParticle(i);
            }
        }

        boolean yer = false;

        @Override
        public void update() {


            if (time > timeToStartAddingParticles && !yer) {
                for (int i = 0; i < numPoints; i++) {
                    particles.addParticle(points[i * 3    ], points[i * 3 + 2]);
                }
                yer = true;
            }

//            if (time > timeToStartAddingParticles && particles.getSize() < numPoints) {
//                for (int i = 0; i < (int) (numPoints * delta / addParticlesDuration); i++) {
//                    if (particles.getSize() < numPoints) addParticle(particles.getSize());
//                }
//            }

//            if (time > timeToStartAddingParticles && !yer) {
//                addParticle2(0);
//                yer = true;
//            }


            for (int i = 0; i < numPoints; i++) {
                updatePoint(points, i);
                if (i < particles.getSize())
                particles.updateParticle(i, points[i * 3    ], points[i * 3 + 2]);
            }

            if (particles2.getSize() > 0) {
                updatePoint(points2, 0);
                particles2.updateParticle(0, points2[0], points2[2]);
            }
        }

        void updatePoint(float[] points, int i) {

            float x, y, z, dx, dy, dz, dt;

            x = points[i * 3    ];
            y = points[i * 3 + 1];
            z = points[i * 3 + 2];

            dx = 10 * (y - x);
            dy = x * (28 - z) - y;
            dz = x * y - (8f / 3f) * z;

            dt = (float) Math.log(time
//                    - particles.getBirthTime(i)
                    + 1) / 8f;
            dx *= dt;
            dy *= dt;
            dz *= dt;

            x += dx * delta;
            y += dy * delta;
            z += dz * delta;

            points[i * 3    ] = x;
            points[i * 3 + 1] = y;
            points[i * 3 + 2] = z;
        }

    }


    static class Traj extends CqScene {

        GraphSpace plotter;
        Particles particles;



        @Override
        public void init() {
            plotter = new GraphSpace(200, 100, 1520, 880, -3, 3, -1, 3);
            addChild(plotter);

            particles = new Particles(10, 1000, 5, 2, new Color("#FFFFFF"));
            plotter.addToGraphSpace(particles);

            plotter.xAxisVisible = false;
            plotter.yAxisVisible = false;

            particles.addParticle(0, 0);
        }

        boolean yer = false;

        @Override
        public void update() {
            float
                    dx = (float) (Math.cos(time) + Math.cos(time * 5)) * delta,
                    dy = (float) (Math.sin(time)) * delta;
            particles.updateParticle(0, particles.getX(0) + dx, particles.getY(0) + dy);
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


    static class PhaseSpace extends CqScene {

        float minX = -10;
        float maxX = 10;
        float minY = -8;
        float maxY = 8;
        float particleRadius = 50;
        int particleCount = 3000;

        GraphSpace graphSpace = new GraphSpace(225, 125, 1920 - 450, 1080 - 250, minX, maxX, minY, maxY);
        VectorField vectorField = new VectorField(minX, maxX, minY, maxY, 0.6f, 15, new Color("#ff3838"), 4);
        Particles particles = new Particles(0, 10, 1, 1, new Color("#FFFF00"));
        Random random = new Random();

        float timeToStartAddingParticles;
        float addParticlesDuration = 3;

        Arrow demoVec;
        Circle demoPoint;
        Text demoText1;
        Text demoText2;
        Text demoVecText1;
        Text demoVecText2;
        float demoPointX = 3;
        float demoPointY = 3;

        public void setDemoX(float x) {
            this.demoPointX = x;
        }

        public void setDemoY(float y) {
            this.demoPointY = y;
        }

        @Override
        public void init() {
            graphSpace.xLabel = "X";
            graphSpace.yLabel = "Y";
            graphSpace.xMarkingInterval = 2;
            graphSpace.yMarkingInterval = 2;
            graphSpace.xMarkingDecimalPlaces = 0;
            graphSpace.yMarkingDecimalPlaces = 0;
            traceObject(graphSpace, 1);

            demoPoint = new Circle(0, 0, 10, new Color("#ff3838"));
            Vec2 pointPos = graphSpace.graphToGlobal(demoPointX, demoPointY);
            demoPoint.setPos(pointPos.x, pointPos.y);

            demoText1 = new Text("");
            demoText2 = new Text("");
            demoVecText1 = new Text("");
            demoVecText2 = new Text("");

            demoVec = new Arrow(0, 0, 0, 0, 0, 7);
            demoVec.setTraceProgress(0);
            demoVec.setColor(new Color("#FFF000"));

            Circle circle = new Circle(10000, 10000, 2, Color.WHITE);
            addChild(circle); // to make the text white?

            popUpObjectSlow(demoText1, 1, 3f);
            popUpObjectSlow(demoText2, 1, 3f);

            popUpObjectSlow(demoVecText1, 1, 9.5f);
            popUpObjectSlow(demoVecText2, 1, 9.5f);

            popUpObjectSlow(demoPoint, 1, 3f);

            Tween.interpolate(demoVec::setTraceProgress, 0, 1, time + 9.5f, 0.8f, Tween.Easing.EASE_IN_OUT);


            graphSpace.addToGraphSpace(demoVec);

            demoText1.fontSize = 30;
            demoText2.fontSize = 30;
            demoText1.offsetY = -15;
            demoText2.offsetY = 15;
            demoText1.offsetX = 25;
            demoText2.offsetX = 25;
            demoText1.alignH = TextAlignH.LEFT;
            demoText2.alignH = TextAlignH.LEFT;
            graphSpace.addToGraphSpace(vectorField);
            vectorField.setTraceProgress(0);

            demoVecText1.fontSize = 30;
            demoVecText2.fontSize = 30;
            demoVecText1.offsetY = -15;
            demoVecText2.offsetY = 15;
            demoVecText1.alignH = TextAlignH.CENTER;
            demoVecText2.alignH = TextAlignH.CENTER;

            Tween.interpolate(this::setDemoX, demoPointX, 5, time + 5, 2, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(this::setDemoY, demoPointY, -4, time + 5, 2, Tween.Easing.EASE_IN_OUT);

            Tween.interpolate(this::setDemoX, 5, -8, time + 7.5f, 2, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(this::setDemoY, -4, 2, time + 7.5f, 2, Tween.Easing.EASE_IN_OUT);

            Tween.interpolate(this::setDemoX, -8, -2, time + 12f, 2, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(this::setDemoY, 2, 1.5f, time + 12f, 2, Tween.Easing.EASE_IN_OUT);

            Tween.interpolate(this::setDemoX, -2, 2, time + 14.5f, 2, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(this::setDemoY, 1.5f, 4, time + 14.5f, 2, Tween.Easing.EASE_IN_OUT);

            Tween.interpolate(this::setDemoX, 2, 2, time + 17f, 2, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(this::setDemoY, 4, -2.5f, time + 17f, 2, Tween.Easing.EASE_IN_OUT);

            Tween.interpolate(demoVecText1::setScaleX, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoVecText1::setScaleY, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoText1::setScaleX, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoText1::setScaleY, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoVecText2::setScaleX, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoVecText2::setScaleY, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoText2::setScaleX, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoText2::setScaleY, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoPoint::setScaleX, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoPoint::setScaleY, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);
            Tween.interpolate(demoVec::setTraceProgress, 1, 0, time + 20, 1, Tween.Easing.EASE_IN_OUT);

            float realX, realY;
            for (int x = 0; x < vectorField.xCount(); x++) {
                for (int y = 0; y < vectorField.yCount(); y++) {
                    realX = vectorField.getX(x);
                    realY = vectorField.getY(y);
                    vectorField.setVec(x, y,
                            dx(realX, realY),
                            dy(realX, realY)
                    );
                }
            }
            Tween.interpolate(vectorField::setTraceProgress, 0, 1, time + 22, 1.5f, Tween.Easing.EASE_IN_OUT);

            graphSpace.addToGraphSpace(particles);
            timeToStartAddingParticles = time + 25;
        }

        public void addParticle() {
            float angle = random.nextFloat() * 2 * (float) Math.PI;
            float radius = (float) Math.pow(random.nextFloat(), 6) * particleRadius;
            particles.addParticle(
                    (float) Math.cos(angle) * radius,
                    (float) Math.sin(angle) * radius
            );
        }

        public float dx(float x, float y) {
            return -y - 0.1f * x;
        }

        public float dy(float x, float y) {
            return x - 0.4f * y;
        }

        @Override
        public void update() {

            Vec2 pointPos = graphSpace.graphToGlobal(demoPointX, demoPointY);

            Vec2 pointer = new Vec2(demoVec.getPointX(), demoVec.getPointY());
            float len = pointer.len();
            pointer.nor().scl(len + 1);

            Vec2 vecPos = graphSpace.graphToGlobal(demoPointX + pointer.x, demoPointY + pointer.y);
            demoPoint.setPos(pointPos.x, pointPos.y);

            demoText1.text = "x = " + String.format("%.1f", demoPointX);
            demoText2.text = "y = " + String.format("%.1f", demoPointY);
            demoText1.setPos(pointPos.x, pointPos.y);
            demoText2.setPos(pointPos.x, pointPos.y);

            demoVecText1.text = "dx = " + String.format("%.1f", demoVec.getPointX());
            demoVecText2.text = "dy = " + String.format("%.1f", demoVec.getPointY());
            demoVecText1.setPos(vecPos.x, vecPos.y);
            demoVecText2.setPos(vecPos.x, vecPos.y);

            demoVec.setPos(demoPointX, demoPointY);
            demoVec.setPointX(dx(demoPointX, demoPointY));
            demoVec.setPointY(dy(demoPointX, demoPointY));


            float x, y, dx, dy, dt;
            for (int i = 0; i < particles.getSize(); i++) {
                x = particles.getX(i);
                y = particles.getY(i);
                dx = dx(x, y);
                dy = dy(x, y);
                dt = (float) Math.log(time - particles.getBirthTime(i) + 1) / 40f;
                particles.updateParticle(i, x + dx * dt, y + dy * dt);
            }

            if (time > timeToStartAddingParticles && particles.getSize() < particleCount) {
                for (int i = 0; i < (int) (particleCount * delta / addParticlesDuration); i++) {
                    addParticle();
                }
            }

        }
    }

    public static class VanDerPol extends CqScene {
        float minX = -6;
        float maxX = 6;
        float minY = -6;
        float maxY = 6;
        float particleRadius = 50;
        int particleCount = 3000;

        GraphSpace graphSpace = new GraphSpace(225, 125, 1920 - 450, 1080 - 250, minX, maxX, minY, maxY);
        VectorField vectorField = new VectorField(minX, maxX, minY, maxY, 0.6f, 15, new Color("#ff3838"), 4);
        Particles particles = new Particles(2, 10, 1, 1, new Color("#FFFF00"));
        Random random = new Random();

        float timeToStartAddingParticles;
        float addParticlesDuration = 3;
        @Override
        public void init() {
            graphSpace.xLabel = "X";
            graphSpace.yLabel = "Y";
            graphSpace.xMarkingInterval = 2;
            graphSpace.yMarkingInterval = 2;
            graphSpace.xMarkingDecimalPlaces = 0;
            graphSpace.yMarkingDecimalPlaces = 0;
            addChild(graphSpace);


            graphSpace.addToGraphSpace(vectorField);
            vectorField.setTraceProgress(0);

            float realX, realY;
            for (int x = 0; x < vectorField.xCount(); x++) {
                for (int y = 0; y < vectorField.yCount(); y++) {
                    realX = vectorField.getX(x);
                    realY = vectorField.getY(y);
                    vectorField.setVec(x, y,
                            dx(realX, realY),
                            dy(realX, realY)
                    );
                }
            }
            Tween.interpolate(vectorField::setTraceProgress, 0, 1, time + 1, 1.5f, Tween.Easing.EASE_IN_OUT);

            graphSpace.addToGraphSpace(particles);
            timeToStartAddingParticles = time + 4;
        }

        public void addParticle() {
            float angle = random.nextFloat() * 2 * (float) Math.PI;
            float radius = (float) Math.pow(random.nextFloat(), 6) * particleRadius;
            particles.addParticle(
                    (float) Math.cos(angle) * radius,
                    (float) Math.sin(angle) * radius
            );
        }

        public float dx(float x, float y) {
            return x - (x * x * x) / 3 - y;
        }

        public float dy(float x, float y) {
            return x;
        }

        @Override
        public void update() {


            float x, y, dx, dy, dt;
            for (int i = 0; i < particles.getSize(); i++) {
                x = particles.getX(i);
                y = particles.getY(i);
                dx = dx(x, y);
                dy = dy(x, y);
                dt = (float) Math.log(time - particles.getBirthTime(i) + 1) / 60f;
                particles.updateParticle(i, x + dx * dt, y + dy * dt);
            }

            if (time > timeToStartAddingParticles && particles.getSize() < particleCount) {
                for (int i = 0; i < (int) (particleCount * delta / addParticlesDuration); i++) {
                    addParticle();
                }
            }

        }
    }

}
