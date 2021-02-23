package diseasesim;

import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.object2d.LineChart;
import com.chalq.util.Color;
import com.chalq.math.MathUtils;

import static com.chalq.core.Cq.*;
import static diseasesim.Dot.*;

public class DiseaseVisual extends CqScene {

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        new CqWindow(config, new DiseaseVisual());
    }

    final Color yellow = new Color("#56B0FF");
    final Color orange = new Color("#FF5151");

    private static final Color COLOR_SUSCEPTIBLE = new Color("#2c90e8");
    private static final Color COLOR_INFECTIOUS = new Color("#e82c2c");
    private static final Color COLOR_RECOVERED = new Color("#4d4d4d");
    private static final Color COLOR_VACCINATED = new Color("#177506");
    LineChart chart;
    DiseaseSim mainSim;

    DiseaseSim[] sims = new DiseaseSim[0];

    float small0w;
    float Rwidth;

    @Override
    public void init() {

        mainSim = new DiseaseSim();
        for (int i = 0; i < sims.length; i++) {
            sims[i] = new DiseaseSim();
        }

        chart = new LineChart(330, 180, 500, 500, "Time Passed", "Population State",  true);
        chart.initLines(COLOR_INFECTIOUS, COLOR_VACCINATED, COLOR_RECOVERED, COLOR_SUSCEPTIBLE);
        chart.fill = true;
        addDrawable(chart);

        small0w = getTextWidth("0", 30) + 1;
        Rwidth = getTextWidth("R", 40) + 1;
    }

    @Override
    public void update() {
//        delta *= 3;

        clearFrame();
        mainSim.update();
        for (DiseaseSim sim : sims) {
            sim.update();
        }
        if (mainSim.toAddToChart) {
            chart.addData(mainSim.iCount, mainSim.vCount, mainSim.rCount, mainSim.sCount);
            mainSim.toAddToChart = false;
        }
        drawSimScene(mainSim);
        drawAvgStats();
    }

    private void drawAvgStats() {
        float sumR0 = 0;
        float sumR0S = 0;
        float sumInfectAge = 0;
        float sumDeathAges = 0;
        
        int rSampleCount = 0;
        int ageCount = 0;
        int deathCount = 0;
        
        if (mainSim.predictedRSamples > 0) {
            rSampleCount++;
            sumR0 += mainSim.avgPredictedR0;
            sumR0S += mainSim.avgPredictedR0S;
        }
        if (mainSim.ageOfInfectionSamples > 0) {
            ageCount++;
            sumInfectAge += mainSim.avgAgeOfInfection;
        }
        if (mainSim.numDeaths > 0) {
            deathCount++;
            sumDeathAges += mainSim.avgDeathAge;
        }

        for (DiseaseSim sim : sims) {

            if (sim.predictedRSamples > 0) {
                rSampleCount++;
                sumR0 += sim.avgPredictedR0;
                sumR0S += sim.avgPredictedR0S;
            }
            if (sim.ageOfInfectionSamples > 0) {
                ageCount++;
                sumInfectAge += sim.avgAgeOfInfection;
            }
            if (sim.numDeaths > 0) {
                deathCount++;
                sumDeathAges += sim.avgDeathAge;
            }
        }

        float avgPredictedR0 = rSampleCount > 0 ? sumR0 / rSampleCount : 0;
        float avgPredictedR0S = rSampleCount > 0 ? sumR0S / rSampleCount : 0;
        float avgInfectAge = ageCount > 0 ? sumInfectAge / ageCount : 0;
        float avgDeathAge = deathCount > 0 ? sumDeathAges / deathCount : 0;


        setColor(yellow);

        float row1Y = 830;
        float row2Y = row1Y + 60;
        float row3Y = row2Y + 60;
        float leftX = 420;
        float rightX = leftX + 190;

        strokeRect(leftX - 20, row1Y - 15, 355, 192, 3);

        
        textSettings(40, TextAlignH.LEFT, TextAlignV.TOP);
        text("R", leftX, row1Y);
        text(": ", leftX + Rwidth + small0w, row1Y);
        text("R", rightX, row1Y);
        text("S: ", rightX + Rwidth + small0w, row1Y);
        text("Age of Infection: ", leftX, row2Y);
        text("Avg Lifespan: ", leftX, row3Y);

        textSettings(30, TextAlignH.LEFT, TextAlignV.TOP);
        text("0", leftX + Rwidth, row1Y + 15);
        text("0", rightX + Rwidth, row1Y + 15);

        setColor(orange);
        text("" + MathUtils.roundPlaces(avgPredictedR0, 1), leftX + Rwidth + small0w + getTextWidth(": ", 40), row1Y);
        text("" + MathUtils.roundPlaces(avgPredictedR0S, 1), rightX + Rwidth + small0w + getTextWidth("S: ", 40), row1Y);
        text("" + MathUtils.roundPlaces(avgInfectAge, 1), leftX + getTextWidth("Age of Infection: ", 40), row2Y);
        text("" + MathUtils.roundPlaces(avgDeathAge, 1), leftX + getTextWidth("Avg Lifespan: ", 40), row3Y);

    }

    public void drawSimScene(DiseaseSim diseaseSim) {
        setColor(Color.WHITE);
        strokeRect(diseaseSim.dotBoundsL, diseaseSim.dotBoundsB, (diseaseSim.dotBoundsR - diseaseSim.dotBoundsL), (diseaseSim.dotBoundsT - diseaseSim.dotBoundsB), 5);
        for (Dot s : diseaseSim.simDots) {
            drawDot(s);
        }

    }

    public void drawDot(Dot dot) {
        if (dot.state == STATE_SUSCEPTIBLE) setColor(COLOR_SUSCEPTIBLE);
        if (dot.state == STATE_INFECTIOUS) setColor(COLOR_INFECTIOUS);
        if (dot.state == STATE_RECOVERED) setColor(COLOR_RECOVERED);
        if (dot.state == STATE_VACCINATED) setColor(COLOR_VACCINATED);
        fillCircle(dot.x, dot.y, 5);

        if (dot.emittingRing) {
            if (dot.state != STATE_INFECTIOUS) {
                dot.emittingRing = false;
            } else {
                float time = Cq.time - dot.startEmitRing;
                float alpha = MathUtils.clamp( -2 * time * (time - 1) , 0, 0.3f);
                setColor(COLOR_INFECTIOUS.r, COLOR_INFECTIOUS.g, COLOR_INFECTIOUS.b, alpha);
                strokeCircle(dot.x, dot.y, 5 + (Cq.time - dot.startEmitRing) * dot.diseaseSim.transRadius, 4);
                if (time > 1) dot.emittingRing = false;
            }
        }

    }
}
