import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.drawables.Arrow;
import com.chalq.drawables.graph.GraphPlotter;
import com.chalq.math.Vec2;
import com.chalq.object2d.Rectangle;
import com.chalq.util.Color;


import static com.chalq.core.Cq.*;

public class SoundWave extends CqScene {


    float width = 800;
    float height = 200;

    GraphPlotter wave;
    Rectangle rect;

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
        wave = new GraphPlotter(getFrameWidth() / 2f - width / 2 - 20, getFrameHeight() / 2f - height / 2 + 150, width, height,
                -2 * (float)Math.PI, 2 * (float)Math.PI, -2, 2);
        wave.addFunction(GraphPlotter.toParametric(this::wave, false), -2 * (float)Math.PI, 2 * (float)Math.PI, Color.WHITE, true);

        addDrawable(wave);
        interpolate(wave.pos, new Vec2(200, 200), time + 2, 2);

        Arrow arrow = new Arrow(300, 50, 100, 20, 5);
        arrow.arcAngle = -270;
        addDrawable(arrow);

        rect = new Rectangle(500, 300, 200, 200);
        addDrawable(rect);
//        interpolate(rect.scale, new Vec2(0.5f, 0.5f), time + 1.5f, 1);
//        interpolate(rect.scale, new Vec2(5f, 5f), time + 2.5f, 1);
        interpolate(rect.traceProgress, 1, time + 1.5f, 2);

    }

    @Override
    public void update() {

        clearFrame();

        int columns = 20;
        int rows = 10;

        float dotsPerPeriod = 10;
        for (int i = 0; i < columns; i++) {
            float offsetX = (float) Math.sin(i / dotsPerPeriod * 2 * Math.PI - time * 3) * width / columns * 0.8f;
            for (int g = 0; g < rows; g++) {
                float dotX = (getFrameWidth() / 2f - width / 2) + width * ((float) i / columns) + offsetX;
                float dotY = (getFrameHeight() / 2f - height / 2) + height * ((float) g / rows) - 80;
                fillCircle(dotX, dotY, 5);
            }
        }

        Cq.arcClockwise(1500, 600, 1500, 500, -270, 4);
        Cq.setColor(1, 0, 0, 1);
        Cq.fillCircle(1500, 600, 10);
        Cq.fillCircle(1500, 500, 10);


    }

    private float wave(float in) {


        return (float) Math.sin(in - time * 3 - Math.PI / 2) / 1.5f;
    }

}
