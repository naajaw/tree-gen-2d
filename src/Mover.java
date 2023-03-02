import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Mover {
    private PApplet p;

    public PVector pos;
    public PVector vel;
    public PVector acc;
    public float mass;
    private float maxSpeed = 4f;
    private float maxForce = 0.1f;

    public Mover(PApplet pApplet, float _mass, float x, float y) {
        p = pApplet;
        mass = _mass;
        pos = new PVector(x, y);
        vel = new PVector(0, 0);
        acc = new PVector(0, 0);
    }

    void applyForce(PVector force) {
        PVector f = PVector.div(force, mass);
        acc.add(f);
    }

    public void seek(PVector target) {
        PVector desired = PVector.sub(target, pos);
        desired.setMag(maxSpeed);
        PVector steer = PVector.sub(desired, vel);
        steer.limit(maxForce);
//        System.out.println("SEEK force:" + steer.x + " " + steer.y);
        applyForce(steer);
    }
    public void separate(ArrayList<Mover> movers) {
        float dsepd = mass * 100;

        PVector sum = new PVector();
        int count = 0;
        for (Mover other: movers) {
            float d = PVector.dist(pos, other.pos);
            if ((d > 0) && (d < dsepd)) {
                PVector diff = PVector.sub(pos, other.pos);
                diff.normalize();
                diff.div(dsepd);
                sum.add(diff);
                count++;
            }
        }
        if (count > 0) {
            sum.div(count);
            sum.setMag(maxSpeed);
            PVector steer = PVector.sub(sum, vel);
            steer.limit(maxForce * 5);
//            System.out.println("SEPARATE force:" + steer.x + " " + steer.y);
            applyForce(steer);
        }
    }

    void update() {
        vel.add(acc);
        pos.add(vel);
        acc.mult(0);
    }

    void display() {
        p.stroke(0);
        p.fill(175);
        p.ellipse(pos.x, pos.y, 16 * mass, 16 * mass);
    }

    void checkEdges() {
        if (pos.x > p.width || pos.x < 0) vel.x *= -1;
        if (pos.y > p.height || pos.y < 0) vel.y *= -1;
    }
}
