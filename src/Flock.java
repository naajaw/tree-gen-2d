import processing.core.PApplet;
import java.util.ArrayList;

public class Flock {
    private PApplet p;
    private ArrayList<Boid> boids;
    private int count;

    public Flock(PApplet _p, int _count) {
        p = _p;
        boids = new ArrayList<>(_count);
        count = _count;
    }

    public void run() {
        for (Boid boid : boids) {
            boid.run(boids, null);
        }
    }

    public void addBoid(Boid boid) {
        boids.add(boid);
    }
}
