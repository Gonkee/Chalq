### Motion graphics animation library for making programmatic visuals
- Uses a lightweight vector graphics rendering library called [NanoVG](https://github.com/memononen/nanovg)
- Uses the [Humble video library](https://github.com/artclarke/humble-video) to save output to mp4

Used to power the animations of many videos on the [Gonkee youtube channel](https://www.youtube.com/@gonkee), such as:
- [How Stable Diffusion Works (AI Image Generation)](https://www.youtube.com/watch?v=sFztPP9qPRc)
- ![position encodings](https://github.com/Gonkee/Chalq/assets/41216697/76c4ca44-0703-4011-8503-186717cb4f84)
- [Chaos Theory: the language of (in)stability](https://www.youtube.com/watch?v=uzJXeluCKMs)
- ![chaos](https://github.com/Gonkee/Chalq/assets/41216697/c19d8dc6-7fd8-447d-a3fa-5ffe3ff6f589)
- [The Math Behind Music and Sound Synthesis](https://www.youtube.com/watch?v=Y7TesKMSE74)
- ![sound waves](https://github.com/Gonkee/Chalq/assets/41216697/65bb345b-990b-48f2-968d-b53924a7f464)

### Code example for a simple neural network animation:
```java
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

```

Here's the result from this code example:

![nn](https://github.com/Gonkee/Chalq/assets/41216697/1348a09e-00c4-43db-8cec-18efe6294d24)


### Here is an example of abstraction and inheritance used to classify renderable objects:

The `Shape2D` abstract class:
```java
package com.chalq.object2d.shape2d;

import com.chalq.core.Object2D;
import com.chalq.object2d.Traceable;
import com.chalq.object2d.path2d.Path2D;
import com.chalq.util.Color;

public abstract class Shape2D extends Object2D implements Traceable {

    public Color fillColor = new Color(1, 1, 1, 0);

    public Path2D outline;

    @Override
    public void setTraceProgress(float progress) {
        outline.setTraceProgress(progress);
    }

    @Override
    public float getTraceProgress() {
        return outline.getTraceProgress();
    }
}
```

The `Polygon` class which inherits it:
```java
package com.chalq.object2d.shape2d;

import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;
import com.chalq.util.Color;

public class Polygon extends Shape2D {

    private final PolyPath path;
    private int vertexCount;

    public Polygon(int vertexCount) {
        this.vertexCount = vertexCount;
        path = new PolyPath(new float[ (vertexCount + 1) * 2]);
        addChild(path);
    }

    public Polygon(float[] vertices) {
        if (vertices.length < 6) throw new IllegalArgumentException("Vertices must contain at least 3 points.");
        if (vertices.length % 2 != 0) throw new IllegalArgumentException("Vertices must have even number of coordinates.");
        vertexCount = vertices.length / 2;

        float[] pathVertices = new float[vertices.length + 2];
        System.arraycopy(vertices, 0, pathVertices, 0, vertices.length);
        pathVertices[pathVertices.length - 2] = vertices[0];
        pathVertices[pathVertices.length - 1] = vertices[1];

        path = new PolyPath(pathVertices);
        addChild(path);
    }

    public void setVertexCoord(int coordID, float val) {
        path.setVertexCoord(coordID, val);
        if (coordID == 0) path.setVertexCoord(vertexCount * 2, val);
        if (coordID == 1) path.setVertexCoord(vertexCount * 2 + 1, val);
    }

    @Override
    public void draw(long nvg) {
        penBeginPath(nvg);
        penMoveTo(nvg, path.getVertexCoord(0), path.getVertexCoord(1));
        for (int coordID = 2; coordID < path.getVertexCount() * 2; coordID += 2) {
            penLineTo(nvg,
                    path.getVertexCoord(coordID),
                    path.getVertexCoord(coordID + 1));
        }
        penSetColor(fillColor);
        penFillPath(nvg);
    }

    @Override
    public void update() {}

    @Override
    public Vec2 getLocalTracePosition() {
        return path.getLocalTracePosition();
    }

    public void setStrokeColor (Color color) {
        path.color = color;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setTraceProgress (float progress) {
        path.setTraceProgress(progress);
    }
}
```
