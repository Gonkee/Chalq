import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.drawables.graph.GraphPlotter;
import com.chalq.math.Vec2;
import com.chalq.util.Color;

public class FunctionTest extends CqScene {

//    FunctionGraph graph = new FunctionGraph(this::f);
    GraphPlotter graph2 = new GraphPlotter(GraphPlotter.toParametric(this::f, false), -5, 5);


    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        new CqWindow(config, new FunctionTest());
    }

    @Override
    public void init() {
    }

    @Override
    public void update() {
        Cq.clearFrame();
//        graph.draw();
        graph2.draw();
    }

    public float f(float x) {
        return (float)( Math.sin(x - Cq.time) + Math.cos(x * 0.7 + Cq.time)) * 5;
    }

    public Vec2 p(float t) {

        float x = (float) Math.cos(t);
        float y = (float) Math.sin(t);
        float scale = (float) Math.sin(t * 4) + 1;

        return new Vec2(scale * x * 10, y * scale * 10);
    }
}
