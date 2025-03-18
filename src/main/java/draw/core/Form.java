package draw.core;

public interface Form {
    void setColor(int r, int g, int b);
    void translate(int dx, int dy);
    void rotate(float angle);
    void setRoundedEdge(float round);
    // Composite pattern
    void add(Form form);
    void remove(Form form);
    Form getChild(int index);
}
