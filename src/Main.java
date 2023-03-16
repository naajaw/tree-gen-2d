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
        tree = new Tree(this, new Stem(this, 10, width/2, height));
//        flowField = new FlowField(this, 10);
    }

    public void draw() {
        background(255);
//        if (frameCount == 1)
//            flowField.debug();
        tree.run();
//        genCount++;
//        if (genCount > 60) {
//            tree.branch();
//            genCount = 0;
//        }
//        System.out.println("frame: " + frameCount);
    }


    /** Processing driver, leave alone */
    public static void main(String[] args) {
        String[] processingArgs = {"Main"};
        Main process = new Main();
        PApplet.runSketch(processingArgs, process);
    }
}