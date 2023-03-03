import processing.core.PApplet;
import java.util.ArrayList;

public class Main extends PApplet {

    private ArrayList<Mover> movers;
    private final int count = 50;
    private final float frictionC = 0.01f;
    private final float dragC = 0.1f;
    private FlowField flowField;

    public void settings() {
        size(3200, 2000);
        movers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            movers.add(new Mover(this, 1, random(width), random(height)));
        }
        flowField = new FlowField(this, 10);
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
//        background(255);
        if (frameCount == 1)
            flowField.debug();

//        PVector wind = new PVector(0.01f, 0);

        for (Mover m: movers) {

//            m.seek(new PVector(mouseX, mouseY));
//            m.wander();
            m.separate(movers);
            m.follow(flowField);

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