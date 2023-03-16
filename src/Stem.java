import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Stem {
    private final PApplet p;

    public float mass;
    public PVector pos;
    public PVector vel;
    public PVector acc;

    private float maxForce = 2f;
    private float maxSpeed = 0.5f;
    private float decayRate = 0.998f;
    private final float neighborDistance = 200;

    private final float lollyStem = 80;
    private final float lollyRadius = 20;
    private final float lollyChange = 0.2f;
    private float wanderTheta = 0.0f;


    public Stem(PApplet pApplet, float _mass, float x, float y) {
        p = pApplet;
        mass = _mass;
        pos = new PVector(x, y);
        vel = new PVector(0, -1);
        acc = new PVector(0, 0);
    }
    public Stem(PApplet pApplet, Stem parent, float angle) {
        p = pApplet;
        mass = parent.mass;
        pos = new PVector(parent.pos.x, parent.pos.y);
        float heading = parent.vel.heading();
        vel = new PVector(
                PApplet.cos(angle + heading),
                PApplet.sin(angle + heading)
        );
        acc = new PVector(0, 0);
    }

    public void run(ArrayList<Stem> others, FlowField flow) {
        applyBehaviors(others, flow);
        update();
        display();
    }

    public void update() {
        vel.add(acc);
        vel.setMag(maxSpeed * mass);
        pos.add(vel);
        debug(vel);
        acc.mult(0);
        checkEdges();
        mass *= decayRate;
    }

    public void display() {
        p.stroke(0);
        p.fill(48);
        p.ellipse(pos.x, pos.y, 10 * mass, 10 * mass);
    }

    void applyForce(PVector force, boolean ignoreMass) {
        float _mass = mass;
        PVector f = PVector.div(force, _mass);
        debugB(f);
        acc.add(f);
    }

    void applyBehaviors(ArrayList<Stem> others, FlowField flow) {
        PVector ris = rise();
        PVector wan = lollyWander();
        PVector sep = separation(others);
        PVector flo = flow != null
                ? follow(flow)
                : null;

        ris.mult(5.0f);
        wan.mult(2f);
        sep.mult(0.5f);
        if (flo != null)
            flo.mult(1.5f);

        applyForce(ris, false);
        applyForce(wan, true);
        applyForce(sep, false);
        if (flo != null)
            applyForce(flo, false);
    }

    public PVector follow(FlowField flow) {
        PVector steer = flow.lookup(pos);
        steer.mult(maxSpeed);
        steer.sub(vel);
        steer.limit(maxForce);
        return steer;

    }

    public PVector lollyWander() {
        wanderTheta = 2;
        PVector predict = vel.copy();
        predict.setMag(lollyStem);
        predict.add(pos);
        float originAngle = vel.heading();
        float x = lollyRadius * PApplet.cos(wanderTheta + originAngle);
        float y = lollyRadius * PApplet.sin(wanderTheta + originAngle);
        PVector target = PVector.add(predict, new PVector(x, y));
        debug(predict, target);
        return seek(target);
    }

//    public PVector windsheildWander() {
//        wanderTheta += p.random(-wanderChange, wanderChange);
//        PVector predict = vel.copy();
//        predict.setMag(wanderDistance);
//        predict.add(pos);
//        float originAngle = vel.heading();
//        float x = wanderRadius * PApplet.cos(wanderTheta + originAngle);
//        float y = wanderRadius * PApplet.sin(wanderTheta + originAngle);
//        PVector target = PVector.add(predict, new PVector(x, y));
//        debug(predict, target);
//        return seek(target);
//    }


    public PVector rise() {
        return seek(new PVector(pos.x, pos.y - 1));
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
            steer.limit(maxForce);
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
    public void debugB(PVector v) {
        PVector scaled = PVector.mult(v, -300);
        PVector relative = PVector.add(pos, scaled);
        p.strokeWeight(5); p.stroke(0, 0, 255);
        p.line(pos.x, pos.y, relative.x, relative.y);
    }
    public void debug(PVector v) {
        PVector scaled = PVector.mult(v, 10);
        PVector relative = PVector.add(pos, scaled);
        p.stroke(0, 255, 0);
        p.line(pos.x, pos.y, relative.x, relative.y);
    }
    public void debug(PVector v, PVector w) {
        p.stroke(255, 0, 0);
        p.line(pos.x, pos.y, v.x, v.y);
        p.line(v.x, v.y, w.x, w.y);
    }

}
