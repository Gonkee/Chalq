package diseasesim;

public class Dot {

    public static final int STATE_SUSCEPTIBLE = 0;
    public static final int STATE_INFECTIOUS = 1;
    public static final int STATE_RECOVERED = 2;

    int state = STATE_SUSCEPTIBLE;
    float x, y;
    float vx, vy;
    int daysInfected = 0;
    int daysRecovered = 0;
    int age = 0;
    final DiseaseSim diseaseSim;
    final int arrayID;

    float startEmitRing = 0;
    boolean emittingRing = false;

    boolean haveBeenInfected = false;
    int ageOfInfection = 0;
    int R0 = 0;
    int R0S = 0;

    public Dot(DiseaseSim diseaseSim, float x, float y, float dir, int arrayID) {
        this.arrayID = arrayID;
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

    public void stateCheck(Dot[] simDots, float elapsed) {
        age++;
        if (age >= diseaseSim.dotLifeSpan) {
            diseaseSim.killDot(arrayID);
            return;
        }

        if (state == STATE_SUSCEPTIBLE) {
            daysInfected = 0;
            daysRecovered = 0;
        } else if (state == STATE_INFECTIOUS) {
            daysRecovered = 0;
            daysInfected++;
            if (daysInfected >= diseaseSim.daysToRecover) state = STATE_RECOVERED;
            for (Dot s : simDots) {
                float dist = (s.x - x) * (s.x - x) + (s.y - y) * (s.y - y);
                if (dist < diseaseSim.transRadius * diseaseSim.transRadius &&
                        diseaseSim.rand.nextFloat() < diseaseSim.infectProbPerDay) {
                    R0++;
                    if (s.state == STATE_SUSCEPTIBLE) {
                        R0S++;
                        s.state = STATE_INFECTIOUS;
                        s.haveBeenInfected = true;
                        s.ageOfInfection = s.age;
                        emitRing(elapsed);
                    }
                }
            }
        } else {
            daysInfected = 0;
            daysRecovered++;
            if (daysRecovered >= diseaseSim.daysToLoseImmunity) state = STATE_SUSCEPTIBLE;
        }
    }


    public void emitRing(float elapsed) {
        emittingRing = true;
        startEmitRing = elapsed;
    }

    public boolean predictedRAvailable() {
        return state == STATE_INFECTIOUS && daysInfected > diseaseSim.daysToRecover / 2;
    }

    public float getPredictedR0() {
        return (float) R0 * diseaseSim.daysToRecover / daysInfected;
    }

    public float getPredictedR0S() {
        return (float) R0S * diseaseSim.daysToRecover / daysInfected;
    }

}
