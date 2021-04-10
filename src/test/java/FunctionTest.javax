import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.object2d.graph.GraphPlotter;
import com.chalq.util.Color;

public class FunctionTest extends CqScene {

//    FunctionGraph graph = new FunctionGraph(this::f);
    GraphPlotter plotter;


    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        config.antialiasing = true;
        new CqWindow(config, new FunctionTest());
    }

    @Override
    public void init() {
        plotter = new GraphPlotter(200, 200, 800, 400, -10, 10, -5, 5);
//        plotter.addFunction(GraphPlotter.toParametric(this::f, false), -10, 10, new Color(1, 0, 0, 1), true);
        plotter.addFunction(GraphPlotter.toParametric(this::g, false), -10, 10, new Color("#56B0FF"), true);
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
