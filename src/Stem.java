import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Stem {
    private final PApplet p;

    public float mass;
    public PVector pos;
    public PVector vel;
    public PVector acc;

    private float maxForce = 0.2f;
    private float maxSpeed = 5f;
    private float decayRate = 0.05f;
    private final float neighborDistance = 200;


    public Stem(PApplet pApplet, float _mass, float x, float y) {
        p = pApplet; mass = _mass;
        vel = new PVector(0, -1);
        acc = new PVector(0, 0);
        pos = new PVector(x, y);
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
//        p.text("  pos: (" + pos.x + ", " + pos.y + ")", pos.x, pos.y);
        p.text("  mass: (" + mass + ")", pos.x, pos.y);
        display();
        update();
    }

    public void display() {
        p.stroke(0);
        p.strokeWeight(2);
        p.fill(0, 0, 0, 0);
        p.ellipse(pos.x, pos.y, 2 * mass, 2 * mass);
    }


    public void update() {
        vel.add(acc);
        System.out.println("maxSpeed: " + maxSpeed + " mass: " + mass + " -> " + maxSpeed * mass);
        vel.setMag(maxSpeed);
        pos.add(vel);
        acc.mult(0);
        checkEdges();
        mass -= decayRate;
    }

    void applyForce(PVector force, String label) {
        PVector f = force; // PVector.div(force, mass);
//        debug(f, label, true, 100, 0, 0, 255);
        acc.add(f);
    }

    void applyBehaviors(ArrayList<Stem> others, FlowField flow) {
        PVector ris = rise();
        PVector wan = windsheildWander();
//        PVector sep = separation(others);
//        PVector flo = follow(flow);

        ris.mult(5.0f);
        wan.mult(6.0f);
//        sep.mult(0.5f);
//        flo.mult(1.5f);

        applyForce(ris, "rise");
        applyForce(wan, "wander");
//        applyForce(sep, "");
//        applyForce(flo);
    }

    public PVector follow(FlowField flow) {
        PVector steer = flow.lookup(pos);
        steer.mult(maxSpeed);
        steer.sub(vel);
        steer.limit(maxForce);
        return steer;

    }

    private float wanderTheta = 0.0f;
    private final float lollyStem = 80;
    private final float lollyRadius = 20;
    private final float lollyChange = 0.2f;
    public PVector lollyWander() {
        wanderTheta = wanderTheta + p.random(-lollyChange, lollyChange);
        PVector predict = vel.copy();
        predict.setMag(lollyStem);
        predict.add(pos);
        float originAngle = vel.heading();
        float x = lollyRadius * PApplet.cos(wanderTheta + originAngle);
        float y = lollyRadius * PApplet.sin(wanderTheta + originAngle);
        PVector target = PVector.add(predict, new PVector(x, y));
        debugAbsolute(target, predict, null, 255, 0, 0);
        debugAbsolute(predict, null, 255, 0, 0);
        return seek(target);
    }

    private final float windshieldChange = 0.1f;
    private final float windshieldRadius = 80f;
    private final float thetaBound = 1.0f;
    public PVector windsheildWander() {
        float change = p.map(p.noise(p.frameCount), 0, 1, -windshieldChange, windshieldChange);

        wanderTheta += change;
        if (wanderTheta > thetaBound) {
            wanderTheta = thetaBound - (wanderTheta - thetaBound);
        }
        if (wanderTheta < -thetaBound) {
            wanderTheta = -thetaBound - (wanderTheta + thetaBound);
        }
        float originAngle = vel.heading();
        float x = windshieldRadius * PApplet.cos(wanderTheta + originAngle);
        float y = windshieldRadius * PApplet.sin(wanderTheta + originAngle);
        PVector target = new PVector(x, y);
        target.add(pos);
        debugAbsolute(target, null, 255, 0, 0);
        p.ellipse(pos.x, pos.y, windshieldRadius * 2, windshieldRadius * 2);
        PVector max = new PVector(
                windshieldRadius * PApplet.cos(thetaBound + originAngle),
                windshieldRadius * PApplet.sin(thetaBound + originAngle)
        );
        debugRelative(max, null, 1, 0, 0, 255);
        PVector min = new PVector(
                windshieldRadius * PApplet.cos(originAngle - thetaBound),
                windshieldRadius * PApplet.sin(originAngle - thetaBound)
        );
        debugRelative(min, null, 1, 0, 0, 255);

        return seek(target);
    }


    public PVector rise() {
        PVector risePoint = new PVector(pos.x, pos.y - 100);
//        debugAbsolute(risePoint, "rise point", 0, 255, 0);
        return seek(risePoint);
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
    public void debugRelative(PVector v, String label, float scale, float r, float g, float b) {
        PVector relative, scaled;
        scaled = PVector.mult(v, scale);
        relative = PVector.add(pos, scaled);
        p.strokeWeight(3); p.stroke(r, g, b);
        p.line(pos.x, pos.y, relative.x, relative.y);
        if (label != null) {
            p.fill(0);
            p.text("  " + label + ": (" + v.x + ", " + v.y + ")", relative.x, relative.y);
        }
    }

    public void debugRelative(PVector v, PVector o, String label, float scale, float r, float g, float b) {
        PVector relative, scaled;
        scaled = PVector.mult(v, scale);
        relative = PVector.add(o, scaled);
        p.strokeWeight(3); p.stroke(r, g, b);
        p.line(o.x, o.y, relative.x, relative.y);
        if (label != null) {
            p.fill(0);
            p.text("  " + label + ": (" + v.x + ", " + v.y + ")", relative.x, relative.y);
        }
    }

    public void debugAbsolute(PVector v, String label, float r, float g, float b) {
        p.strokeWeight(3);
        p.stroke(r, g, b);
        p.fill(0, 0, 0, 0);
        p.line(pos.x, pos.y, v.x, v.y);
        p.ellipse(v.x, v.y, 10, 10);
        if (label != null) {
            p.fill(0);
            p.text("  " + label + ": (" + v.x + ", " + v.y + ")", v.x, v.y);
        }
    }

    public void debugAbsolute(PVector v, PVector o, String label, float r, float g, float b) {
        p.strokeWeight(3);
        p.stroke(r, g, b);
        p.fill(0, 0, 0, 0);
        p.line(o.x, o.y, v.x, v.y);
        p.ellipse(v.x, v.y, 10, 10);
        if (label != null) {
            p.fill(0);
            p.text("  " + label + ": (" + v.x + ", " + v.y + ")", v.x, v.y);
        }
    }
}
