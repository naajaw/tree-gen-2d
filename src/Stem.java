import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Stem {
    private final PApplet p;

    public PVector pos;
    public PVector vel;
    public PVector acc;
    public float mass;

    private float decayRate = 0.02f;
    private final float neighborDistance = 200;
    private float maxSpeed = 0.75f;
    private float maxForce = 1f;

    private final float wanderDistance = 80;
    private final float wanderRadius = 60;
    private final float wanderChange = 0.2f;
    private float wanderTheta = 0.0f;


    public Stem(PApplet pApplet, float _mass, float x, float y) {
        p = pApplet;
        mass = _mass;
        pos = new PVector(x, y);
        vel = new PVector(p.random(-2, 2), p.random(-2, -1));
        acc = new PVector(0, 0);
    }
    public Stem(PApplet pApplet, Stem parent) {
        p = pApplet;
        mass = parent.mass;
        pos = new PVector(parent.pos.x, parent.pos.y);
        vel = new PVector(parent.vel.x, parent.vel.y); // TODO: parameterize initial branch angle!
        acc = new PVector(0, 0);
    }

    public void run(ArrayList<Stem> others, FlowField flow) {
        applyBehaviors(others, flow);
        update();
        display();
    }

    public void update() {
        vel.add(acc);
        pos.add(vel.setMag(maxSpeed * mass));
        acc.mult(0);
        checkEdges();
        mass -= decayRate;
    }

    public void display() {
        p.stroke(0);
        p.fill(48);
        p.ellipse(pos.x, pos.y, 3 * mass, 3 * mass);
    }

    void applyForce(PVector force) {
        PVector f = PVector.div(force, mass);
        acc.add(f);
    }

    void applyBehaviors(ArrayList<Stem> others, FlowField flow) {
        PVector ris = rise();
        PVector wan = wander();
        PVector sep = separation(others);
        PVector ali = alignment(others);
        PVector coh = cohesion(others);
        PVector flo = flow != null
                ? follow(flow)
                : null;

        ris.mult(1.0f);
        wan.mult(5.0f);
        sep.mult(2.0f);
        ali.mult(1.0f);
        coh.mult(1.0f);
        if (flo != null)
            flo.mult(1.5f);

        applyForce(ris);
        applyForce(wan);
        applyForce(sep);
        applyForce(ali);
        applyForce(coh);
        if (flo != null)
            applyForce(flo);
    }

    public PVector follow(FlowField flow) {
        PVector steer = flow.lookup(pos);
        steer.mult(maxSpeed);
        steer.sub(vel);
        steer.limit(maxForce);
        return steer;
    }

    public PVector wander() {
        wanderTheta += p.random(-wanderChange, wanderChange);
        PVector predict = vel.copy();
        predict.setMag(wanderDistance);
        predict.add(pos);
        float originAngle = vel.heading();
        float x = wanderRadius * PApplet.cos(wanderTheta + originAngle);
        float y = wanderRadius * PApplet.sin(wanderTheta + originAngle);
        PVector target = PVector.add(predict, new PVector(x, y));
//        debug(predict, target);
        return seek(target);
    }

    public PVector rise() {
        return seek(new PVector(pos.x, pos.y -1));
    }

    public PVector separation(ArrayList<Stem> others) {
        float sepDistance = mass * 10;

        PVector sum = new PVector(0, 0);
        PVector diff = new PVector();
        int count = 0;
        for (Stem other: others) {
            float d = PVector.dist(pos, other.pos);
            if ((d > 0) && (d < sepDistance)) {
                diff.x = pos.x - other.pos.x;
                diff.y = pos.y - other.pos.y;
                diff.normalize();
                diff.div(d);
                sum.add(diff);
                count++;
            }
        }
        if (count > 0) {
            sum.div(count);
            sum.setMag(maxSpeed);
            PVector steer = PVector.sub(sum, vel);
            steer.limit(maxForce * 5);
            return steer;
        }
        return new PVector(0, 0);
    }

    public PVector cohesion(ArrayList<Stem> others) {
        PVector sum = new PVector(0, 0);
        int count = 0;
        for (Stem other : others) {
            float d = PVector.dist(pos, other.pos);
            if (d > 0 & d < neighborDistance) {
                sum.add(other.pos);
                count++;
            }
        }
        if (count > 0) {
            sum.div(count);
            return seek(sum);
        }
        return new PVector(0, 0);
    }

    public PVector alignment(ArrayList<Stem> others) {
        PVector steer = new PVector(0, 0);
        int count = 1;
        for (Stem other : others) {
            float d = PVector.dist(pos, other.pos);
            if ((d > 0) && (d < neighborDistance)) {
                steer.add(other.vel);
                count++;
            }
        }
        if (count > 0) {
            steer.div(count);
            steer.normalize();
            steer.mult(maxSpeed);
            steer.sub(vel);
            steer.limit(maxForce);
            return steer;
        }
        return new PVector(0, 0);
    }

    public PVector seek(PVector target) {
        PVector steer = PVector.sub(target, pos);
        steer.setMag(maxSpeed);
        steer.sub(vel);
        steer.limit(maxForce);
        return steer;
    }


    public void checkEdges() {
        if (pos.x > p.width) pos.x = 0;
        if (pos.x < 0) pos.x = p.width;
        if (pos.y > p.height) pos.y = 0;
        if (pos.y < 0) pos.y = p.height;
    }

    // ----------------------------------------------------------------------------------------------------------- debug
    public void debug(PVector v) {
        PVector relative = PVector.add(pos, v);
        p.stroke(0);
        p.line(pos.x, pos.y, relative.x, relative.y);
    }
    public void debug(PVector v, PVector w) {
        p.stroke(0);
        p.line(pos.x, pos.y, v.x, v.y);
        p.line(v.x, v.y, w.x, w.y);
    }

}
