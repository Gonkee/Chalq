import com.chalq.core.*;
import com.chalq.math.MathUtils;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.object2d.path2d.Line;
import com.chalq.object2d.shape2d.Circle;
import com.chalq.util.Color;

public class NeuralNetworkAnim extends CqScene{

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color("#000000");
        config.antialiasing = true;
        config.crfFactor = 15;
        new CqWindow(config, new NeuralNetworkAnim());
    }

    class Layer {
        int numNodes;
        float x, startY, endY;
        Layer(int numNodes, float x, float gapY) {
            this.numNodes = numNodes;
            this.x = x;
            float ySpan = gapY * (numNodes - 1);
            this.startY = 1080 / 2 - ySpan / 2;
            this.endY = 1080 / 2 + ySpan / 2;
        }

        void trace(float traceStartTime) {
            for (int i = 0; i < numNodes; i++) {
                float y = MathUtils.lerp(startY, endY, i / (float) (numNodes - 1));
                Circle c = new Circle(x, y, 30, new Color(0, 0, 0, 1));
                addChild(c);
                ArcPath ap = new ArcPath(x, y, 30,  0, 0.001f, true);
                traceObject(ap, traceStartTime + i * 0.1f);
            }
        }

        float getNodeY(int nodeID) {
            return MathUtils.lerp(startY, endY, nodeID / (float) (numNodes - 1));
        }
    }

    Layer l1, l2, l3;

    @Override
    public void init() {
        l1 = new Layer(3, 960 - 300, 120);
        l2 = new Layer(5, 960, 120);
        l3 = new Layer(3, 960 + 300, 120);
        traceConnections(l1, l2, 2);
        traceConnections(l2, l3, 3);
        l1.trace(1.0f);
        l2.trace(1.2f);
        l3.trace(1.4f);
    }

    void traceConnections(Layer from, Layer to, float traceStartTime) {
        for (int fromID = 0; fromID < from.numNodes; fromID++) {
            for (int toID = 0; toID < to.numNodes; toID++) {
                traceObject(new Line(
                        from.x / 2, from.getNodeY(fromID) / 2,
                        to.x - from.x, to.getNodeY(toID) - from.getNodeY(fromID)
                ), traceStartTime + fromID * 0.2f + toID * 0.1f);
            }
        }
    }
}
