import com.chalq.core.*;
import com.chalq.math.MathUtils;
import com.chalq.object2d.*;
import com.chalq.object2d.path2d.ArcPath;
import com.chalq.object2d.path2d.Line;
import com.chalq.object2d.shape2d.Circle;
import com.chalq.object2d.shape2d.Rectangle;
import com.chalq.util.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StableDiffusionAnim {


    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color("#000000");
        config.antialiasing = true;
//        config.outputMP4Path = "vidout/3x3layer.mp4";
        config.crfFactor = 15;
        new CqWindow(config, new Sinusoidal());

    }


    static class Convolution extends CqScene {


        BufferedImage img;
        EmptyObject input = new EmptyObject();

        int x1 = 960 - 320 - 480, y1 = 220, x2 = 960 - 320 + 480, y2 = 220, pixelSize = 40;

        Rectangle output = new Rectangle(x2, y2, pixelSize, pixelSize);
        int pixelID = 0;

        @Override
        public void init() {

            try {
                img = ImageIO.read(new File("E:\\23) image generation\\dev\\pickaxe.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Rectangle bg1 = new Rectangle(x1, y1, 16 * pixelSize, 16*pixelSize);
            Rectangle bg2 = new Rectangle(x2, y2, 16 * pixelSize, 16*pixelSize);
            bg1.fillColor = new Color(0, 0, 0, 1);
            bg2.fillColor = new Color(0, 0, 0, 1);
            addChild(bg1);
            addChild(bg2);

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {

                    // non convoluted display
                    Color c = getColor(x, y);
                    Rectangle r = new Rectangle(x1 + x * pixelSize, y1 + y * pixelSize, pixelSize, pixelSize);
                    r.fillColor = c;
                    r.setTraceProgress(0);
                    addChild(r);

                    // convolute
                    Color c1 = getColor(x - 1, y - 1);
                    Color c2 = getColor(x - 0, y - 1);
                    Color c3 = getColor(x + 1, y - 1);
                    Color c4 = getColor(x - 1, y - 0);
                    Color c5 = getColor(x + 1, y - 0);
                    Color c6 = getColor(x - 1, y + 1);
                    Color c7 = getColor(x - 0, y + 1);
                    Color c8 = getColor(x + 1, y + 1);

                    // blur kernel
//                    float totalR = (c1.r + c2.r + c3.r + c4.r + c5.r + c6.r + c7.r + c8.r) * 0.1f + c.r * 0.2f;
//                    float totalG = (c1.g + c2.g + c3.g + c4.g + c5.g + c6.g + c7.g + c8.g) * 0.1f + c.g * 0.2f;
//                    float totalB = (c1.b + c2.b + c3.b + c4.b + c5.b + c6.b + c7.b + c8.b) * 0.1f + c.b * 0.2f;

                    // vertical edge
//                    float totalR = (c1.r + c4.r + c6.r) * -0.5f + (c2.r + c.r + c7.r) * 1 + (c3.r + c5.r + c8.r) * -0.5f;
//                    float totalG = (c1.g + c4.g + c6.g) * -0.5f + (c2.g + c.g + c7.g) * 1 + (c3.g + c5.g + c8.g) * -0.5f;
//                    float totalB = (c1.b + c4.b + c6.b) * -0.5f + (c2.b + c.b + c7.b) * 1 + (c3.b + c5.b + c8.b) * -0.5f;

                    // horizontal edge
                    float totalR = (c1.r + c2.r + c3.r) * -0.5f + (c4.r + c.r + c5.r) * 1 + (c6.r + c7.r + c8.r) * -0.5f;
                    float totalG = (c1.g + c2.g + c3.g) * -0.5f + (c4.g + c.g + c5.g) * 1 + (c6.g + c7.g + c8.g) * -0.5f;
                    float totalB = (c1.b + c2.b + c3.b) * -0.5f + (c4.b + c.b + c5.b) * 1 + (c6.b + c7.b + c8.b) * -0.5f;
//                    float totalR = (c2.r) * -0.5f + (c.r) * 1 + (c7.r) * -0.5f;
//                    float totalG = (c2.g) * -0.5f + (c.g) * 1 + (c7.g) * -0.5f;
//                    float totalB = (c2.b) * -0.5f + (c.b) * 1 + (c7.b) * -0.5f;



//                    float totalR = (c1.r + c2.r + c3.r + c4.r + c5.r + c6.r + c7.r + c8.r) * 0.1f + c.r * 0.2f;
//                    float totalG = (c1.g + c2.g + c3.g + c4.g + c5.g + c6.g + c7.g + c8.g) * 0.1f + c.g * 0.2f;
//                    float totalB = (c1.b + c2.b + c3.b + c4.b + c5.b + c6.b + c7.b + c8.b) * 0.1f + c.b * 0.2f;

                    Color total = new Color(totalR, totalG, totalB, 1);

                    Rectangle r_conv = new Rectangle(x2 + x * pixelSize, y2 + y * pixelSize, pixelSize, pixelSize);
                    r_conv.fillColor = total;
                    r_conv.setTraceProgress(0);
                    addChild(r_conv);

                }
            }

            addChild(input);

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    Rectangle r = new Rectangle( (x - 1) * pixelSize, (y - 1) * pixelSize, pixelSize, pixelSize);
                    input.addChild(r);
                    input.setPos(x1, y1);
                }
            }

            addChild(output);


        }

        @Override
        public void update() {

            pixelID = (int) (Cq.time * 3);

            int px = pixelID % 12 + 2;
            int py = pixelID / 12 + 2;

            input.setPos(x1 + px * pixelSize, y1 + py * pixelSize);
            output.setPos(x2 + px * pixelSize, y2 + py * pixelSize);

        }

        Color getColor(int x, int y) {

            if (x >= 0 && x < 16 && y >= 0 && y < 16) {

                int color = img.getRGB(x, y);

                int r = (color & 0xff0000) >> 16;
                int g = (color & 0xff00) >> 8;
                int b = color & 0xff;

                return new Color((float) r / 255, (float) g / 255, (float) b / 255, 1f);
            } else {
                return new Color(0, 0, 0, 1);
            }

        }

    }


    static class Sinusoidal extends CqScene {

        float minX = 0;
        float maxX = 15;
        float minY = -1.5f;
        float maxY = 1.5f;

        GraphSpace graphSpace = new GraphSpace(200, 100, 750, 500, minX, maxX, minY, maxY);



        Arrow demoVec;

        Line intersect = new Line((minX + 0.3f) / 2, minY / 2, 0, maxY - minY);
        Circle i0 = new Circle(0, 0, 8, new Color(1.0f, 0.1f, 0.1f, 1.0f));
        Circle i1 = new Circle(0, 0, 8, new Color(0.1f, 1.0f, 0.1f, 1.0f));
        Circle i2 = new Circle(0, 0, 8, new Color(0.1f, 0.1f, 1.0f, 1.0f));
        Circle i3 = new Circle(0, 0, 8, new Color(1.0f, 1.0f, 0.1f, 1.0f));
        Circle i4 = new Circle(0, 0, 8, new Color(1.0f, 0.1f, 1.0f, 1.0f));
        Circle i5 = new Circle(0, 0, 8, new Color(0.1f, 1.0f, 1.0f, 1.0f));


        Text t0 = new Text("0");
        Text t1 = new Text("1");
        Text t2 = new Text("2");
        Text t3 = new Text("3");
        Text t4 = new Text("4");
        Text t5 = new Text("5");

        Text inText = new Text("in");


        int d = 6;
        int n = 8;

        public float w0(float pos) { return (float) Math.sin( pos / Math.pow(n, (double) 0/d) ); }
        public float w1(float pos) { return (float) Math.cos( pos / Math.pow(n, (double) 0/d) ); }
        public float w2(float pos) { return (float) Math.sin( pos / Math.pow(n, (double) 2/d) ); }
        public float w3(float pos) { return (float) Math.cos( pos / Math.pow(n, (double) 2/d) ); }
        public float w4(float pos) { return (float) Math.sin( pos / Math.pow(n, (double) 4/d) ); }
        public float w5(float pos) { return (float) Math.cos( pos / Math.pow(n, (double) 4/d) ); }

        FunctionGraph g0 = new FunctionGraph(this::w0, minX, maxX, new Color(1.0f, 0.1f, 0.1f, 1.0f), 500, true, false);
        FunctionGraph g1 = new FunctionGraph(this::w1, minX, maxX, new Color(0.1f, 1.0f, 0.1f, 1.0f), 500, true, false);
        FunctionGraph g2 = new FunctionGraph(this::w2, minX, maxX, new Color(0.1f, 0.1f, 1.0f, 1.0f), 500, true, false);
        FunctionGraph g3 = new FunctionGraph(this::w3, minX, maxX, new Color(1.0f, 1.0f, 0.1f, 1.0f), 500, true, false);
        FunctionGraph g4 = new FunctionGraph(this::w4, minX, maxX, new Color(1.0f, 0.1f, 1.0f, 1.0f), 500, true, false);
        FunctionGraph g5 = new FunctionGraph(this::w5, minX, maxX, new Color(0.1f, 1.0f, 1.0f, 1.0f), 500, true, false);


        @Override
        public void init() {
            graphSpace.xLabel = "x";
            graphSpace.yLabel = "y";
            graphSpace.xMarkingInterval = 15;
            graphSpace.yMarkingInterval = 1.5f;
            graphSpace.xMarkingDecimalPlaces = 0;
            graphSpace.yMarkingDecimalPlaces = 1;
            addChild(graphSpace);



            demoVec = new Arrow(500, 800, 100, 0, 0, 5);
            demoVec.setColor(new Color("#ffffff"));


            addChild(t0);
            addChild(t1);
            addChild(demoVec);
            addChild(t2);
            addChild(t3);
            addChild(t4);
            addChild(t5);

            addChild(inText);


            graphSpace.addToGraphSpace(g0);
            graphSpace.addToGraphSpace(g1);
            graphSpace.addToGraphSpace(g2);
            graphSpace.addToGraphSpace(g3);
            graphSpace.addToGraphSpace(g4);
            graphSpace.addToGraphSpace(g5);

            graphSpace.addToGraphSpace(intersect);

            graphSpace.addToGraphSpace(i0);
            graphSpace.addToGraphSpace(i1);
            graphSpace.addToGraphSpace(i2);
            graphSpace.addToGraphSpace(i3);
            graphSpace.addToGraphSpace(i4);
            graphSpace.addToGraphSpace(i5);

            t0.fontSize     = 40;
            t1.fontSize     = 40;
            t2.fontSize     = 40;
            t3.fontSize     = 40;
            t4.fontSize     = 40;
            t5.fontSize     = 40;
            inText.fontSize = 40;
            inText.alignH = Cq.TextAlignH.LEFT;

            t0.setPos(750, 700 + 0 * 50);
            t1.setPos(750, 700 + 1 * 50);
            t2.setPos(750, 700 + 2 * 50);
            t3.setPos(750, 700 + 3 * 50);
            t4.setPos(750, 700 + 4 * 50);
            t5.setPos(750, 700 + 5 * 50);

            inText.setPos(300, 700 + 2 * 50);

            t0.color = new Color(1.0f, 0.1f, 0.1f, 1.0f);
            t1.color = new Color(0.1f, 1.0f, 0.1f, 1.0f);
            t2.color = new Color(0.1f, 0.1f, 1.0f, 1.0f);
            t3.color = new Color(1.0f, 1.0f, 0.1f, 1.0f);
            t4.color = new Color(1.0f, 0.1f, 1.0f, 1.0f);
            t5.color = new Color(0.1f, 1.0f, 1.0f, 1.0f);

        }


        @Override
        public void update() {

            float inX = intersect.getX() * 2;

            i0.setPos(inX, w0(inX));
            i1.setPos(inX, w1(inX));
            i2.setPos(inX, w2(inX));
            i3.setPos(inX, w3(inX));
            i4.setPos(inX, w4(inX));
            i5.setPos(inX, w5(inX));

            inText.text = "input: " + String.format("%.2f", inX);

            t0.text = String.format("%.2f", w0(inX));
            t1.text = String.format("%.2f", w1(inX));
            t2.text = String.format("%.2f", w2(inX));
            t3.text = String.format("%.2f", w3(inX));
            t4.text = String.format("%.2f", w4(inX));
            t5.text = String.format("%.2f", w5(inX));

            inX += 0.005f;
            intersect.setX(inX / 2);
        }
    }

}
