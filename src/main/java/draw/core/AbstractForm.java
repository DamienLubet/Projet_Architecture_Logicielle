package draw.core;

public class AbstractForm {
    private int r, g, b; // color
    private float x, y; // position
    private float rotate; // rotation
    private float center_rotate; // center of rotation
    private float translate; // translation
    private float round; // roundness


    public AbstractForm(int x, int y, int r, int g, int b) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
        this.rotate = 0;
        this.center_rotate = 0;
        this.translate = 0;
        this.round = 0;
    }

    public void setColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void rotate(float angle) {
        rotate += angle;
    }

    public void setRoundedEdge(float round) {
        this.round = round;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int[] getColor() {
        return new int[] {r, g, b};
    }

    
    
}
