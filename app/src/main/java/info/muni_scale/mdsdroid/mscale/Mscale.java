package info.muni_scale.mdsdroid.mscale;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mscale implements Serializable {

    private float number;
    private String[] characteristics;
    private String[] obstacles;
    private String[] underground;
    private String slopePercent;
    private static Mscale[] mscales;
    private static List<Float> ordinals = new ArrayList<>(10);

    static {
        mscales = new Mscale[] {
                new Mscale(0).setSlopePercent("< 20")
                        .setCharacteristics("90° turns within > 2 m and with slope < 10 %")
                        .setObstacles("no obstacles")
                        .setUnderground("pavement or solid soil/compact gravel"),
                new Mscale(1).setSlopePercent("< 40")
                        .setCharacteristics("90° turn within > 1 m and with slope < 20 %")
                        .setObstacles("small obstacles, approx. 5cm high (small stones, flat roots)", "single 15 cm steps")
                        .setUnderground("partly loose soil/gravel"),
                new Mscale(2).setSlopePercent("< 60")
                        .setCharacteristics("90° turn within > 0.5 m and with slope < 30 %")
                        .setObstacles("obstacles, approx. 10 cm high (stones, roots", "single 30 cm steps")
                        .setUnderground("loose soil/gravel"),
                new Mscale(3).setSlopePercent("< 80")
                        .setCharacteristics("135° turn within ~ 0.5 m and with slope < 40 %")
                        .setObstacles("obstacles that are approx 20cm high (stones, roots)", "several irregular steps, approx. 20 cm each", "drops < 1 m", "gaps < 0.5 m")
                        .setUnderground("loose soil with loose stones (size of few cm)"),
                new Mscale(4).setSlopePercent("< 100")
                        .setCharacteristics("135° turn within ~ 0.5 m and with slope < 60 %")
                        .setObstacles("big obstacles (stones, logs ~ 30 cm)", "several irregular steps ~ 30 cm each", "drops < 1.5 m", "gaps < 1 m")
                        .setUnderground("very loose/slippery soil with loose stones (size of several cm)"),
                new Mscale(5).setSlopePercent("> 100")
                        .setCharacteristics("135° turn within ~ 0.5 m and with slope < 80 %")
                        .setObstacles("very big obstacles (stones, logs ~ 40 cm)", "several irregular steps ~ 40 cm each", "drops > 1.5 m", "gaps > 1 m")
                        .setUnderground("very loose/slippery soil with loose stones (size of several cm)")
        };

        for(float num = 0; num <= 5; num += 0.5) {
            ordinals.add(num);
        }
    }



    public static Mscale[] getCollection() {
        return mscales;
    }

    public static List<Float> getAllowedOrdinals() {
        return ordinals;
    }

    public Mscale(float number) {
        this.number = number;
    }

    public String[] getCharacteristics() {
        return characteristics;
    }

    public Mscale setCharacteristics(String... characteristics) {
        this.characteristics = characteristics;
        return this;
    }

    public String[] getObstacles() {
        return obstacles;
    }

    public Mscale setObstacles(String... obstacles) {
        this.obstacles = obstacles;
        return this;
    }

    public String[] getUnderground() {
        return underground;
    }

    public Mscale setUnderground(String... underground) {
        this.underground = underground;
        return this;
    }

    public String getSlopePercent() {
        return slopePercent;
    }

    public Mscale setSlopePercent(String slopePercent) {
        this.slopePercent = slopePercent;
        return this;
    }

    public float getNumber() {

        return number;
    }

    public Mscale setNumber(float number) {
        this.number = number;
        return this;
    }

    @Override
    public String toString() {
        return "M" + number;

    }
}
