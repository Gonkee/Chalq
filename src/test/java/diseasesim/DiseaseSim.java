package diseasesim;

import com.chalq.core.Cq;

import java.util.ArrayList;
import java.util.Random;

public class DiseaseSim {

    Random rand = new Random();
    Dot[] simDots;

    final int totalPopulation = 1000;
    final float infectProbPerDay = 0.03f;
    final float transRadius = 30;
    final int initialInfectedCount = 10;
    final int daysPerSec = 3;
    final int daysToRecover = 20;
    final int daysToLoseImmunity = 20000;
    final int dotLifeSpan = 200;
    final boolean randomizeAges = true;

    int daysPassed = 0;
    float checkStateTimeElapsed = 0;
    float dotBoundsL = 960 + 100;
    float dotBoundsR = 1920 - 100;
    float dotBoundsB = 540 - (dotBoundsR - dotBoundsL) / 2;
    float dotBoundsT = 540 + (dotBoundsR - dotBoundsL) / 2;

    // external access
    boolean toAddToChart = false;
    int sCount = 0;
    int iCount = 0;
    int rCount = 0;
    float avgPredictedR0 = 0;
    float avgPredictedR0S = 0;
    float avgAgeOfInfection = 0;

    public DiseaseSim() {
        simDots = new Dot[totalPopulation];
        for (int i = 0; i < simDots.length; i++) {
            simDots[i] = newDot(i);
            if (randomizeAges) simDots[i].age = rand.nextInt(dotLifeSpan);
            if (i < initialInfectedCount) simDots[i].state = Dot.STATE_INFECTIOUS;
        }
    }

    private Dot newDot(int i) {
        float x = dotBoundsL + (dotBoundsR - dotBoundsL) * rand.nextFloat();
        float y = dotBoundsB + (dotBoundsT - dotBoundsB) * rand.nextFloat();
        float dir = rand.nextFloat() * 2 * (float) Math.PI;
        return new Dot(this, x, y, dir, i);
    }

    public void update() {

        checkStateTimeElapsed += Cq.delta;
        boolean checkState = checkStateTimeElapsed > 1f / daysPerSec;
        if (checkState) {
            checkStateTimeElapsed = 0;
            daysPassed++;

            sCount = 0;
            iCount = 0;
            rCount = 0;

            float sumR0 = 0;
            float sumR0S = 0;
            int predictedRSamples = 0;

            int ageOfInfectionSamples = 0;
            int sumAgeOfInfection = 0;

            for (Dot s : simDots) {
                if (s.state == Dot.STATE_SUSCEPTIBLE) sCount++;
                if (s.state == Dot.STATE_INFECTIOUS) iCount++;
                if (s.state == Dot.STATE_RECOVERED) rCount++;
                if (s.state == Dot.STATE_RECOVERED) {
                    sumR0 += s.R0;
                    sumR0S += s.R0S;
                    predictedRSamples++;
                }
                if (s.state == Dot.STATE_INFECTIOUS) {
                    sumAgeOfInfection += s.ageOfInfection;
                    ageOfInfectionSamples++;
                }
            }

            if (predictedRSamples == 0) predictedRSamples = 1;
            avgPredictedR0 = sumR0 / predictedRSamples;
            avgPredictedR0S = sumR0S / predictedRSamples;

            if (ageOfInfectionSamples == 0) ageOfInfectionSamples = 1;
            avgAgeOfInfection = (float) sumAgeOfInfection / ageOfInfectionSamples;

            toAddToChart = true;

        }

        for (Dot s : simDots) {
            s.update(Cq.delta, Cq.time);
            if (checkState) s.stateCheck(simDots, Cq.time);
        }
    }

    public void killDot(int id) {
        simDots[id] = newDot(id);
    }

}
