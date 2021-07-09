import com.chalq.core.*;
import com.chalq.math.MathUtils;
import com.chalq.math.Vec2;
import com.chalq.object2d.path2d.PolyPath;
import com.chalq.object2d.shape2d.Circle;
import com.chalq.object2d.shape2d.Polygon;
import com.chalq.object2d.shape2d.Rectangle;
import com.chalq.util.Color;

import java.util.Random;

public class Ocean {


    public static void main(String[] args) {
        CqConfig config = new CqConfig();
        config.width = 1920;
        config.height = 1080;
        config.backgroundColor = new Color("#1A2333");
        config.antialiasing = true;
        config.outputMP4Path = "vidout/seaIce.mp4";
        config.crfFactor = 15;
        new CqWindow(config, new SeaIce());

    }


    static class SeaIce extends CqScene {

        Polygon ocean;
        Rectangle ice;
        Circle[] salt;
        float[] sVelX;
        float[] sVelY;

        @Override
        public void init() {

            int surfaceDetail = 300;

            float[] vertices = new float[surfaceDetail * 2 + 4];
            ocean = new Polygon(vertices);
            setOceanShape();

            ocean.fillColor = new Color(
                    0 / 255f,
                    110 / 255f,
                    179 / 255f,
                    0.5f);
//            ocean.setStrokeColor(
//                    new Color(
//                    0 / 255f,
//                    44 / 255f,
//                    71 / 255f,
//                    0.5f)
//            );

            ice = new Rectangle(960, 430, 300, 200);
            ice.fillColor = new Color("#ededed");
            ice.setOffset(-150, -50);
            ice.setScale(0.2f, 0.2f);


            addChild(ice);
            addChild(ocean);

            Random rand = new Random();
            salt = new Circle[1000];
            sVelX = new float[1000];
            sVelY = new float[1000];
            for (int i = 0; i < salt.length; i ++) {
                float rX = rand.nextFloat() * 2 - 1;
                float rXo = rX;
                if (rX < 0) rX = - rX * rX;
                else rX =  rX * rX;
                rX = (rX + rXo) / 2;


                float rY = rand.nextFloat();
                rY *= rY;
                salt[i] = new Circle( 960 + rX * 900, 480 + rY * 520, 3, Color.WHITE);
                addChild(salt[i]);
            }


            Tween.interpolate(ice::setScaleX, 0.2f, 2, Cq.time, 10, Tween.Easing.LINEAR);
            Tween.interpolate(ice::setScaleY, 0.2f, 2, Cq.time, 10, Tween.Easing.LINEAR);
        }

        @Override
        public void update() {
            setOceanShape();
            ice.setY(430 + (float) Math.sin(Cq.time * 1.5) * 10);

            float lb = 960 - 150 * ice.getScaleX() - 10;
            float rb = 960 + 150 * ice.getScaleX() + 10;
            float bb = 430 + 150 * ice.getScaleY() + 10;

            float vX, vY;
            Vec2 repel = new Vec2();
            for (int i = 0; i < salt.length; i ++) {
                Circle c = salt[i];
                vX = (float) Math.sin(i     ) * 3;
                vY = (float) Math.cos(i / 2f) * 3;

                float x = c.getX();
                float y = c.getY();

                if (y < 450) sVelY[i] += 3;

                if (x < rb && x > lb && y < bb) {
                    repel.set(x - 960, y - 430).nor().scl(20);
                    sVelX[i] += repel.x;
                    sVelY[i] += repel.y;
                }

                c.setPos( c.getX() + (sVelX[i] + vX) * Cq.delta, c.getY() + (sVelY[i] + vY) * Cq.delta );
                sVelX[i] *= 0.997f;
                sVelY[i] *= 0.997f;
            }
        }

        void setOceanShape() {

            float leftX = -50;
            float rightX = 2000;
            float bottomY = 1200;
            float topY = 430;
            float amplitude = 15;

            ocean.setVertexCoord(0, rightX);
            ocean.setVertexCoord(1, bottomY);
            ocean.setVertexCoord(2, leftX);
            ocean.setVertexCoord(3, bottomY);

            float x;
            for (int id = 4; id < ocean.getVertexCount() * 2; id += 2) {
                x = MathUtils.lerp(leftX, rightX, (id - 4) / (ocean.getVertexCount() * 2 - 2 - 4f));
                ocean.setVertexCoord(id, x);
                ocean.setVertexCoord(id + 1, topY + waveVal(x) * amplitude);
            }

        }

        float waveVal(float in) {

            double ting = 0;
//            in *= 2;
            ting += Math.sin(in / 100f + Cq.time);
            ting += Math.sin(in / 80f - Cq.time / 1.5) * 0.7f;
            ting += Math.sin(in / 300f - Cq.time) * 0.1f;
            ting += Math.sin(in / 60f - Cq.time * 2) * 0.2f;
            ting += Math.sin(in / 30f + Cq.time * 3) * 0.2f;


            return (float) ting;
        }

    }

}
