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
    private float maxForce = 0.9f;

    private float wanderDistance = 80;
    private float wanderRadius = 60;

    public Mover(PApplet pApplet, float _mass, float x, float y) {
        p = pApplet;
        mass = _mass;
        pos = new PVector(x, y);
        vel = new PVector(p.random(-2, 2), p.random(-2, 2));
        acc = new PVector(0, 0);
    }

    void applyForce(PVector force) {
        PVector f = PVector.div(force, mass);
        acc.add(f);
    }

    void applyBehaviors(ArrayList<Mover> others, FlowField flow) {
        PVector sep = separate(others);
        PVector coh = cohesion(others);
        PVector ali = align(others);
//        PVector follow = follow(flow);

        sep.mult(1.5f);
        ali.mult(1.0f);
        coh.mult(1.0f);
//        follow.mult(0.5f);

        applyForce(sep);
        applyForce(ali);
        applyForce(coh);
    }

    public PVector seek(PVector target) {
        PVector steer = PVector.sub(target, pos);
        steer.setMag(maxSpeed);
        steer.sub(vel);
        steer.limit(maxForce);
//        System.out.println("SEEK force: " + steer.x + " " + steer.y);
        return steer;
    }

    public PVector follow(FlowField flow) {
        PVector steer = flow.lookup(pos);
        steer.mult(maxSpeed);
        steer.sub(vel);
        steer.limit(maxForce);
        return steer;
    }

    public PVector wander() {
        PVector predict = vel.copy();
        predict.setMag(wanderDistance);
        predict.add(pos);
        float r = wanderRadius;
        float theta = p.random(2 * p.PI);
        float x = r * PApplet.cos(theta);
        float y = r * PApplet.sin(theta);
//        System.out.println("wander diff: " + x + " " + y);
        PVector target = PVector.add(predict, new PVector(x, y));
//        debug(predict, target);
        return seek(target);
    }

    public PVector separate(ArrayList<Mover> others) {
        float sepDistance = mass * 100;

        PVector sum = new PVector();
        PVector diff = new PVector();
        int count = 0;
        for (Mover other: others) {
            float d = PVector.dist(pos, other.pos);
            if ((d > 0) && (d < sepDistance)) {
                diff.x = pos.x - other.pos.x;
                diff.y = pos.y - other.pos.y;
                diff.normalize();
                diff.div(sepDistance);
                sum.add(diff);
                count++;
            }
        }
        if (count > 0) {
            sum.div(count);
            sum.setMag(maxSpeed);
            PVector steer = PVector.sub(sum, vel);
            steer.limit(maxForce * 5);
//          System.out.println("SEPARATE force:" + steer.x + " " + steer.y);
            return steer;
        }
        return new PVector(0, 0);
    }

    public PVector cohesion(ArrayList<Mover> others) {

    }

    public PVector align(ArrayList<Mover> others) {
        float neighborRadius = 50;
        PVector steer = new PVector(0, 0);
        int count = 1;
        for (Mover other : others) {
            float d = PVector.dist(pos, other.pos);
            if ((d > 0) && (d < neighborRadius)) {
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

    public void update() {
        vel.add(acc);
        pos.add(vel);
        acc.mult(0);
        checkEdges();
    }

    public void display() {
        p.stroke(0);
        p.fill(48);
        p.ellipse(pos.x, pos.y, 16 * mass, 16 * mass);
    }

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


    void checkEdges() {
        if (pos.x > p.width) pos.x = 0;
        if (pos.x < 0) pos.x = p.width;
        if (pos.y > p.height) pos.y = 0;
        if (pos.y < 0) pos.y = p.height;
    }
}
