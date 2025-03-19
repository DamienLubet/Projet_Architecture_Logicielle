package draw.core;

public class Rectangle extends AbstractForm {
    private int width, height;
    
    public Rectangle(int x, int y, int width, int height, int r, int g, int b) {
        super(x, y, r, g, b);
        this.width = width;
        this.height = height;
    }
    
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    
    
}
