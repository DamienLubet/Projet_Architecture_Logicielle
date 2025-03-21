package draw.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import draw.core.AbstractForm;
import draw.core.Rectangle;
import draw.core.RegularPolygon;

public class DrawingApp extends Frame {
    private final List<AbstractForm> shapes = new ArrayList<>();
    private final Canvas drawingCanvas;
    private Canvas trashCanvas;
    private AbstractForm selectedShape = null;
    private Point lastMousePosition = null;

    public DrawingApp() {
        setupFrame();
        Panel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        Panel trashPanel = createTrashPanel();
        add(trashPanel, BorderLayout.WEST);
        drawingCanvas = createDrawingCanvas();
        add(drawingCanvas, BorderLayout.CENTER);
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Drawing App");
        setSize(900, 650);
        setLayout(new BorderLayout()); 
        setLocationRelativeTo(null); // center the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                System.exit(0); // exit the application
            }
        });
    }

    // Create a toolbar with two buttons for Rectangle and Polygon
    private Panel createToolbar() {
        Panel toolbar = new Panel(new FlowLayout());
        Button rectButton = createStyledButton("Rectangle", new Color(0, 120, 215));
        Button polyButton = createStyledButton("Polygon", new Color(220, 50, 50));
        new DragSource().createDefaultDragGestureRecognizer(rectButton, DnDConstants.ACTION_COPY, new DragGestureHandler("Rectangle"));
        new DragSource().createDefaultDragGestureRecognizer(polyButton, DnDConstants.ACTION_COPY, new DragGestureHandler("Polygon"));
        toolbar.add(rectButton);
        toolbar.add(polyButton);
        return toolbar;
    }

    // Create a panel with a trash can icon
    private Panel createTrashPanel() {
        Panel trashPanel = new Panel(new FlowLayout());
        Image trashImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/trash.png"));
        trashCanvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(trashImage, 0, 0, this);
            }
        };
        trashCanvas.setPreferredSize(new Dimension(50, 50));
        trashPanel.add(trashCanvas);
        return trashPanel;
    }

    // Create a canvas for drawing shapes
    private Canvas createDrawingCanvas() {
        Canvas canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                for (AbstractForm shape : shapes) {
                    g.setColor(new Color(shape.getColor()[0], shape.getColor()[1], shape.getColor()[2]));
                    if (shape instanceof Rectangle rect) {
                        g.fillRoundRect((int) rect.getX(), (int) rect.getY(), rect.getWidth(), rect.getHeight(), 15, 15);
                        g.setColor(Color.BLACK);
                        g.drawRoundRect((int) rect.getX(), (int) rect.getY(), rect.getWidth(), rect.getHeight(), 15, 15);
                    } else if (shape instanceof RegularPolygon poly) {
                        drawPolygon(g, poly);
                    }
                }
            }
        };
        canvas.setBackground(Color.WHITE);
        canvas.setPreferredSize(new Dimension(900, 600));
        new DropTarget(canvas, new ShapeDropTargetListener());
        addCanvasListeners(canvas);
        return canvas;
    }

    // Add mouse listeners to the canvas
    private void addCanvasListeners(Canvas canvas) {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (int i = shapes.size() - 1; i >= 0; i--) {
                    AbstractForm shape = shapes.get(i);
                    if (isInsideShape(shape, e.getPoint())) {
                        selectedShape = shape;
                        lastMousePosition = e.getPoint();
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedShape != null && isInsideTrash(e.getPoint())) {
                    removeShape(selectedShape);
                }
                selectedShape = null;
                lastMousePosition = null;
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape != null && lastMousePosition != null) {
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;
                    selectedShape.translate(dx, dy);
                    lastMousePosition = e.getPoint();
                    canvas.repaint();
                }
            }
        });
    }

    // Create a button with the given text and background color
    private Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Add a shape to the canvas
    private void addShape(AbstractForm shape) {
        shapes.add(shape);
        drawingCanvas.repaint();
    }

    // Remove a shape from the canvas
    private void removeShape(AbstractForm shape) {
        shapes.remove(shape);
        drawingCanvas.repaint();
    }

    // Draw a regular polygon
    private void drawPolygon(Graphics g, RegularPolygon poly) {
        int[] xPoints = new int[poly.getnbSides()];
        int[] yPoints = new int[poly.getnbSides()];
        double angle = 2 * Math.PI / poly.getnbSides();
        for (int i = 0; i < poly.getnbSides(); i++) {
            xPoints[i] = (int) (poly.getX() + poly.getSideLength() * Math.cos(i * angle));
            yPoints[i] = (int) (poly.getY() + poly.getSideLength() * Math.sin(i * angle));
        }
        g.fillPolygon(xPoints, yPoints, poly.getnbSides());
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints, yPoints, poly.getnbSides());
    }

    // Check if a point is inside a shape
    private boolean isInsideShape(AbstractForm shape, Point p) {
        if (shape instanceof Rectangle rect) {
            return p.x >= rect.getX() && p.x <= rect.getX() + rect.getWidth()
                    && p.y >= rect.getY() && p.y <= rect.getY() + rect.getHeight();
        } else if (shape instanceof RegularPolygon poly) {
            double dx = p.x - poly.getX();
            double dy = p.y - poly.getY();
            return dx * dx + dy * dy <= poly.getSideLength() * poly.getSideLength();
        }
        return false;
    }

    // Check if a point is inside the trash can
    private boolean isInsideTrash(Point p) {
        Point trashLoc = trashCanvas.getLocationOnScreen();
        Point canvasLoc = drawingCanvas.getLocationOnScreen();
        int tx = trashLoc.x - canvasLoc.x;
        int ty = trashLoc.y - canvasLoc.y;
        return p.x >= tx && p.x <= tx + trashCanvas.getWidth()
                && p.y >= ty && p.y <= ty + trashCanvas.getHeight();
    }

    private class DragGestureHandler implements DragGestureListener {
        private final String shapeType;
        public DragGestureHandler(String shapeType) {
            this.shapeType = shapeType;
        }
        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
            Transferable transferable = new ShapeTransferable(shapeType);
            dge.startDrag(DragSource.DefaultCopyDrop, transferable);
        }
    }

    private static class ShapeTransferable implements Transferable {
        private final String shapeType;
        private static final DataFlavor FLAVOR = DataFlavor.stringFlavor;
        public ShapeTransferable(String shapeType) {
            this.shapeType = shapeType;
        }
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{FLAVOR};
        }
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return FLAVOR.equals(flavor);
        }
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (FLAVOR.equals(flavor)) {
                return shapeType;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    private class ShapeDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = dtde.getTransferable();
                String shapeType = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                Point dropPoint = dtde.getLocation();
                if (shapeType.equals("Rectangle")) {
                    addShape(new Rectangle(dropPoint.x, dropPoint.y, 120, 60, 0, 0, 255));
                } else if (shapeType.equals("Polygon")) {
                    addShape(new RegularPolygon(dropPoint.x, dropPoint.y, 6, 50, 255, 0, 0));
                }
                dtde.dropComplete(true);
            } catch (Exception ex) {
                dtde.rejectDrop();
            }
        }
    }
}