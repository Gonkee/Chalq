import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.math.LerpVal;
import com.chalq.math.MathUtils;
import com.chalq.object2d.graph.GraphPlotter;
import com.chalq.util.Color;

import static com.chalq.core.Cq.*;
import static com.chalq.core.Cq.time;

public class TestScenes {

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        config.antialiasing = true;
        new CqWindow(config, new Scene2());
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

        @Override
        public void init() {
            {
                GraphPlotter p1 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p1.addFunction(this::w1c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p1.setAxesVisible(false);
                addChild(p1);
                lerpPos(p1, p1.getX(), p1.getY() - 350, time + 3, 1.5f);
            }
            {
                GraphPlotter p2 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p2.addFunction(this::w2c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p2.setAxesVisible(false);
                addChild(p2);
                lerpPos(p2, p2.getX(), p2.getY() - 350 / 3f, time + 3, 1.5f);
            }
            {
                GraphPlotter p3 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p3.addFunction(this::w3c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p3.setAxesVisible(false);
                addChild(p3);
                lerpPos(p3, p3.getX(), p3.getY() + 350 / 3f, time + 3, 1.5f);
            }
            {
                GraphPlotter p4 = new GraphPlotter(getFrameWidth() / 2f - 700 / 2f, getFrameHeight() / 2f - 400 / 2f, 700, 400, 0, 2 * (float) Math.PI, -5, 5);
                p4.addFunction(this::w4c, 0, 2 * (float) Math.PI, Color.WHITE, true);
                p4.setAxesVisible(false);
                addChild(p4);
                lerpPos(p4, p4.getX(), p4.getY() + 350, time + 3, 1.5f);
            }
            lerpCustom(morphFac, 1, time + 3, 1.5f);
        }
        LerpVal morphFac = new LerpVal();
        private float w1(float in) { return (float) Math.sin((in - time * 1.5) * 1 ) * 0.7f; }
        private float w2(float in) { return (float) Math.sin((in - time * 1.5) * 2 ) * 0.7f; }
        private float w3(float in) { return (float) Math.sin((in - time * 1.5) * 3 ) * 0.7f; }
        private float w4(float in) { return (float) Math.sin((in - time * 1.5) * 4 ) * 0.7f; }
        private float wsum(float in) { return w1(in) + w2(in) + w3(in) + w4(in); }
        private float w1c(float in) { return MathUtils.lerp( wsum(in), w1(in), morphFac.val); }
        private float w2c(float in) { return MathUtils.lerp( wsum(in), w2(in), morphFac.val); }
        private float w3c(float in) { return MathUtils.lerp( wsum(in), w3(in), morphFac.val); }
        private float w4c(float in) { return MathUtils.lerp( wsum(in), w4(in), morphFac.val); }
    }

}
