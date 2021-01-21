package diseasesim;

import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.drawables.LineChart;
import com.chalq.util.Color;
import com.chalq.util.MathUtils;

import static com.chalq.core.Cq.*;
import static diseasesim.Dot.*;

public class DiseaseVisual extends CqScene {

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        CqWindow window = new CqWindow(config, new DiseaseVisual());
    }

    private static final Color SUSCEPTIBLE = new Color("#2c90e8");
    private static final Color INFECTIOUS = new Color("#e82c2c");
    private static final Color RECOVERED = new Color("#4d4d4d");
    LineChart chart;
    DiseaseSim diseaseSim;

    @Override
    public void init() {

        diseaseSim = new DiseaseSim();

        chart = new LineChart(250, 230, 600, 600, "Time Passed", "Population State",  true);
        chart.initLines(INFECTIOUS, RECOVERED, SUSCEPTIBLE);
        chart.fill = true;
    }

    @Override
    public void update() {
        clearFrame();
        diseaseSim.update();
        if (diseaseSim.toAddToChart) {
            chart.addData(diseaseSim.iCount, diseaseSim.rCount, diseaseSim.sCount);
            diseaseSim.toAddToChart = false;
        }
        drawSimScene(diseaseSim);
    }

    public void drawSimScene(DiseaseSim diseaseSim) {
        setColor(Color.WHITE);
        strokeRect(diseaseSim.dotBoundsL, diseaseSim.dotBoundsB, (diseaseSim.dotBoundsR - diseaseSim.dotBoundsL), (diseaseSim.dotBoundsT - diseaseSim.dotBoundsB), 5);
        for (Dot s : diseaseSim.simDots) {
            drawDot(s);
        }

        chart.draw();
        textSettings(30, TextAlignH.LEFT, TextAlignV.TOP);
        text("R0: " + MathUtils.roundPlaces(diseaseSim.avgPredictedR0, 2), 250, 950);
        text("R0S: " + MathUtils.roundPlaces(diseaseSim.avgPredictedR0S, 2), 450, 950);
    }

    public void drawDot(Dot dot) {
        if (dot.state == STATE_SUSCEPTIBLE) setColor(SUSCEPTIBLE);
        if (dot.state == STATE_INFECTIOUS) setColor(INFECTIOUS);
        if (dot.state == STATE_RECOVERED) setColor(RECOVERED);
        fillCircle(dot.x, dot.y, 5);

        if (dot.emittingRing) {
            if (dot.state != STATE_INFECTIOUS) {
                dot.emittingRing = false;
            } else {
                float time = Cq.time - dot.startEmitRing;
                float alpha = MathUtils.clamp( -2 * time * (time - 1) , 0, 0.3f);
                setColor(INFECTIOUS.r, INFECTIOUS.g, INFECTIOUS.b, alpha);
                strokeCircle(dot.x, dot.y, 5 + (Cq.time - dot.startEmitRing) * dot.diseaseSim.transRadius, 4);
                if (time > 1) dot.emittingRing = false;
            }
        }

    }
}
