package draw.core;

public class RegularPolygon extends AbstractForm {
    private int nbSides; // number of nbSides
    private int sideLength; // length of each side
    
    public RegularPolygon(int x, int y, int nbSides, int sideLength, int r, int g, int b) {
        super(x, y, r, g, b); 
        this.nbSides = nbSides;
        this.sideLength = sideLength;
    }
    
    public void setnbSides(int nbSides) {
        this.nbSides = nbSides;
    }
    
    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }
    
    public int getnbSides() {
        return nbSides;
    }
    
    public int getSideLength() {
        return sideLength;
    }
}