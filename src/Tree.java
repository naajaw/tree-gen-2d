import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;

public class Tree {
    private final PApplet p;
    private ArrayList<Stem> stems;

    public Tree(PApplet _p, Stem seed) {
        p = _p;
        stems = new ArrayList<>(1);
        stems.add(seed);
    }

    public void run() {
        Iterator<Stem> it = stems.iterator();
        while (it.hasNext()) {
            Stem stem = it.next();
            stem.run(stems, null);
            if (stem.mass <= 2)
                it.remove();
        }
    }

    private float branchAngle = 0.2f;
    public void branch() {
//        ArrayList<Stem> buds = new ArrayList<>(stems.size());
//        for (Stem stem : stems) {
//            buds.add(new Stem(p, stem));
//        }
//        stems.addAll(buds);
        if (stems.size() > 0) {
            Stem trunk = stems.get(0);
            stems.add(new Stem(p, trunk, branchAngle));
            branchAngle *= -1;
        }
    }
}
