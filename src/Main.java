import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Main extends PApplet {

    private ArrayList<Mover> movers;
    private final float frictionC = 0.01f;
    private final float dragC = 0.1f;

    public void settings() {
        size(1600, 1000);
        movers = new ArrayList<>(50);
        for (int i = 0; i < 50; i++) {
            movers.add(new Mover(this, 1, random(width), random(height)));
        }
    }


//    public PVector getFriction(Mover mover) {
//        PVector friction = mover.vel.copy();
//        friction.mult(-1);
//        friction.normalize();
//        friction.mult(frictionC);
//        return friction;
//    }
//
//    public PVector getDrag(Mover mover) {
//        float speed = mover.vel.mag();
//        float mag = dragC * speed * speed;
//        PVector drag = mover.vel.copy();
//        drag.mult(-1);
//        drag.normalize();
//        drag.mult(mag);
//        return drag;
//    }
    public void draw() {
        background(255);

//        PVector wind = new PVector(0.01f, 0);

        for (Mover m: movers) {

            m.seek(new PVector(mouseX, mouseY));
            m.separate(movers);

            m.update();
            m.display();
//            Mover mover = movers[i];
//            mover.applyForce(wind);
//            PVector gravity = new PVector(0, 0.1f * mover.mass);
//            mover.applyForce(gravity);
//            mover.applyForce(getFriction(mover));
//
//            mover.update();
//            mover.display();
//            mover.checkEdges();
        }
    }


    /** Processing driver, leave alone */
    public static void main(String[] args) {
        String[] processingArgs = {"Main"};
        Main process = new Main();
        PApplet.runSketch(processingArgs, process);
    }
}