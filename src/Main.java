import processing.core.PApplet;
import java.util.ArrayList;

public class Main extends PApplet {

    private Flock flock;
    private final int count = 100;
    private final float frictionC = 0.01f;
    private final float dragC = 0.1f;
    private FlowField flowField;

    public void settings() {
        size(3200, 2000);
        flock = new Flock(this, count);
        for (int i = 0; i < count; i++) {
            flock.addBoid(new Boid(this, 10, width/2, height/2));
        }
//        flowField = new FlowField(this, 10);
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
//        if (frameCount == 1)
//            flowField.debug();
        flock.run();
        System.out.println("frame: " + frameCount);
    }


    /** Processing driver, leave alone */
    public static void main(String[] args) {
        String[] processingArgs = {"Main"};
        Main process = new Main();
        PApplet.runSketch(processingArgs, process);
    }
}