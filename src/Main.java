import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {

    private Tree tree;
    private final int count = 5;
    private final float frictionC = 0.01f;
    private final float dragC = 0.1f;
    private FlowField flowField;

    private float genCount = 0;

    public void settings() {
        size(3200, 2000);
        tree = new Tree(this, new Stem(this, 10, width/2, height/2));
//        flowField = new FlowField(this, 10);
    }

    public void draw() {
        frameRate(4);
        background(255);
        reticle();
//        PVector mouse = new PVector(mouseX, mouseY);
//        PVector center = new PVector(width/2, height/2);
//        mouse.sub(center);
//        translate(width/2, height/2);
//        line(0, 0, mouse.x, mouse.y);

        textSize(32);
        tree.run();
//        genCount++;
//        if (genCount > 60) {
//            tree.branch();
//            genCount = 0;
//        }
//        System.out.println("frame: " + frameCount);
    }

    void reticle() {
        stroke(0);
        strokeWeight(2);
        fill(0, 0, 0, 0);
        line(width/2, height/2, width/2, 20 + (height/2));
        line(width/2, height/2, 20 + (width/2), height/2);
    }


    /** Processing driver, leave alone */
    public static void main(String[] args) {
        String[] processingArgs = {"Main"};
        Main process = new Main();
        PApplet.runSketch(processingArgs, process);
    }
}