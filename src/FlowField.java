import processing.core.PApplet;
import processing.core.PVector;

public class FlowField {
    private PApplet p;
    PVector[][] field;
    int rows, cols;
    int resolution;

    public FlowField(PApplet _p, int resolution_) {
        p = _p;
        resolution = resolution_;
        cols = p.width / resolution;
        rows = p.height / resolution;
        field = new PVector[cols][rows];

        float xoffset = 0;
        for (int i = 0; i < cols; i++) {
            float yoffset = 0;
            for (int j = 0; j < rows; j++) {
                float theta = PApplet.map(p.noise(xoffset, yoffset), 0, 1, 0, p.TWO_PI);
                field[i][j] = new PVector(PApplet.cos(theta), PApplet.sin(theta));
                yoffset += 0.1;
            }
            xoffset += 0.1;
        }
    }

    PVector lookup(PVector pos) {
        int col = (int)(PApplet.constrain(pos.x / resolution, 0f, cols - 1));
        int row = (int)(PApplet.constrain(pos.y / resolution, 0f, rows - 1));
        return field[col][row].copy();
    }

    public void debug() {
        PVector o = new PVector(0, 0);
        PVector v = new PVector(0, 0);
        p.stroke(0);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                o.x = i * resolution;
                o.y = j * resolution;
                v.x = o.x + (field[i][j].x * resolution);
                v.y = o.y + (field[i][j].y * resolution);
                p.line(o.x, o.y, v.x, v.y);
            }
        }
    }

}
