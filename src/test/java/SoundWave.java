import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.drawables.graph.GraphPlotter;
import com.chalq.util.Color;

import static com.chalq.core.Cq.*;

public class SoundWave extends CqScene {


    float width = 800;
    float height = 200;

    GraphPlotter wave;

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
        wave.addFunction(GraphPlotter.toParametric(this::wave, false), -2 * (float)Math.PI, 2 * (float)Math.PI, Color.WHITE);
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
        wave.updateAllGraphs();
        wave.draw();
    }

    private float wave(float in) {
        return (float) Math.sin(in - time * 3 - Math.PI / 2) / 1.5f;
    }

}
