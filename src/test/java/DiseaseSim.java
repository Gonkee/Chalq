import com.chalq.core.Cq;
import com.chalq.core.CqConfig;
import com.chalq.core.CqScene;
import com.chalq.core.CqWindow;
import com.chalq.drawables.LineChart;
import com.chalq.util.Color;
import com.chalq.util.MathUtils;

import java.util.Random;

import static com.chalq.core.Cq.*;

public class DiseaseSim extends CqScene {

    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color(0.102f, 0.137f, 0.2f, 1f);
        CqWindow window = new CqWindow(config, new DiseaseSim());
    }

    static final Color SUSCEPTIBLE = new Color("#2c90e8");
    static final Color INFECTIOUS = new Color("#e82c2c");
    static final Color RECOVERED = new Color("#4d4d4d");

    static class SimDot {

        float x, y;
        float vx, vy;
        int daysInfected = 0;
        int daysRecovered = 0;
        final DiseaseSim diseaseSim;

        static final int STATE_SUSCEPTIBLE = 0;
        static final int STATE_INFECTIOUS = 1;
        static final int STATE_RECOVERED = 2;

        int state = STATE_SUSCEPTIBLE;

        float startEmitRing = 0;
        boolean emittingRing = false;

        public SimDot(DiseaseSim diseaseSim, float x, float y, float dir) {
            this.diseaseSim = diseaseSim;
            this.x = x;
            this.y = y;
            this.vx = (float) Math.cos(dir) * 50;
            this.vy = (float) Math.sin(dir) * 50;
        }

        public void update(float delta, float elapsed) {
            x += delta * vx;
            y += delta * vy;

            if (x < diseaseSim.dotBoundsL) {
                x = diseaseSim.dotBoundsL;
                if (vx < 0) vx *= -1;
            }
            if (x > diseaseSim.dotBoundsR) {
                x = diseaseSim.dotBoundsR;
                if (vx > 0) vx *= -1;
            }
            if (y < diseaseSim.dotBoundsB) {
                y = diseaseSim.dotBoundsB;
                if (vy < 0) vy *= -1;
            }
            if (y > diseaseSim.dotBoundsT) {
                y = diseaseSim.dotBoundsT;
                if (vy > 0) vy *= -1;
            }
        }

        public void stateCheck(SimDot[] simDots, float elapsed) {
            if (state == STATE_SUSCEPTIBLE) {
                daysInfected = 0;
                daysRecovered = 0;
                for (SimDot s : simDots) {
                    if (s.state == STATE_INFECTIOUS) {
                        float dist = (s.x - x) * (s.x - x) + (s.y - y) * (s.y - y);
                        if (dist < diseaseSim.transRadius * diseaseSim.transRadius &&
                                diseaseSim.rand.nextFloat() < diseaseSim.infectProbPerDay) {
                            state = STATE_INFECTIOUS;
                            s.emitRing(elapsed);
                            break;
                        }
                    }
                }
            } else if (state == STATE_INFECTIOUS) {
                daysRecovered = 0;
                daysInfected++;
                if (daysInfected >= diseaseSim.daysToRecover) state = STATE_RECOVERED;
            } else {
                daysInfected = 0;
                daysRecovered++;
                if (daysRecovered >= diseaseSim.daysToLoseImmunity) state = STATE_SUSCEPTIBLE;
            }
        }

        public void draw() {
            if (state == STATE_SUSCEPTIBLE) setColor(DiseaseSim.SUSCEPTIBLE);
            if (state == STATE_INFECTIOUS) setColor(DiseaseSim.INFECTIOUS);
            if (state == STATE_RECOVERED) setColor(DiseaseSim.RECOVERED);
            fillCircle(x, y, 5);

            if (emittingRing) {
                if (state != STATE_INFECTIOUS) {
                    emittingRing = false;
                } else {
                    float time = Cq.time - startEmitRing;
                    float alpha = MathUtils.clamp( -2 * time * (time - 1) , 0, 0.3f);
                    setColor(INFECTIOUS.r, INFECTIOUS.g, INFECTIOUS.b, alpha);
                    fillCircle(x, y, 5 + (Cq.time - startEmitRing) * diseaseSim.transRadius);
                    if (time > 1) emittingRing = false;
                }
            }

        }

        public void emitRing(float elapsed) {
            emittingRing = true;
            startEmitRing = elapsed;
        }


    }


    final int totalPopulation = 1000;
    final float infectProbPerDay = 0.10f;
    final float transRadius = 30;
    final int initialInfectedCount = 10;
    final int daysPerSec = 5;
    final int daysToRecover = 30;
    final int daysToLoseImmunity = 20000;

    final float effectiveInfectProb = 1 - (float) Math.pow(1 - infectProbPerDay, 1f / (60f / daysPerSec));
    int daysPassed = 0;
    float checkStateTimeElapsed = 0;
    float dotBoundsL = 960 + 100;
    float dotBoundsR = 1920 - 100;
    float dotBoundsB = 540 - (dotBoundsR - dotBoundsL) / 2;
    float dotBoundsT = 540 + (dotBoundsR - dotBoundsL) / 2;

    Random rand = new Random();
    SimDot[] simDots;

    LineChart chart;

    public DiseaseSim() {

        System.out.println("effective prob: " + effectiveInfectProb);
        simDots = new SimDot[totalPopulation];
        for (int i = 0; i < simDots.length; i++) {

            float x = dotBoundsL + (dotBoundsR - dotBoundsL) * rand.nextFloat();
            float y = dotBoundsB + (dotBoundsT - dotBoundsB) * rand.nextFloat();
//            System.out.println("x, y: " + x + " , " + y);
            float dir = rand.nextFloat() * 2 * (float) Math.PI;

            simDots[i] = new SimDot(this, x, y, dir);
            if (i < initialInfectedCount) simDots[i].state = SimDot.STATE_INFECTIOUS;
        }

        chart = new LineChart(150, 100, 600, 600, "Time Passed", "Population State",  true);
        chart.initLines(INFECTIOUS, SUSCEPTIBLE, RECOVERED);
//        chart.topAndBottomSpace = true;
//        chart.yAxisStartAtZero = true;
        chart.fill = true;
    }



    @Override
    public void update() {

        checkStateTimeElapsed += Cq.delta;
        boolean checkState = checkStateTimeElapsed > 1f / daysPerSec;
        if (checkState) {
            checkStateTimeElapsed = 0;
            daysPassed++;

            int sCount = 0;
            int iCount = 0;
            int rCount = 0;

            for (SimDot s : simDots) {
                if (s.state == SimDot.STATE_SUSCEPTIBLE) sCount++;
                if (s.state == SimDot.STATE_INFECTIOUS) iCount++;
                if (s.state == SimDot.STATE_RECOVERED) rCount++;
            }

            chart.addData(iCount, sCount, rCount);

        }

        for (SimDot s : simDots) {
            s.update(Cq.delta, Cq.time);
            if (checkState)
                s.stateCheck(simDots, Cq.time);
        }


        clearFrame();
        setColor(Color.WHITE);
        strokeRect(dotBoundsL, dotBoundsB, (dotBoundsR - dotBoundsL), (dotBoundsT - dotBoundsB), 5);
        for (SimDot s : simDots) {
            s.draw();
        }

        chart.draw();

    }
}
