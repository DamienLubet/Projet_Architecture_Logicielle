package draw.ui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import draw.core.AbstractForm;
import draw.core.Rectangle;
import draw.core.RegularPolygon;

public class DrawingApp extends Frame {
    private final List<AbstractForm> shapes = new ArrayList<>();
    private final Canvas drawingCanvas;

    public DrawingApp() {
        setTitle("Drawing App");
        setSize(900, 650);
        // Close the window
        addWindowListener((WindowListener) new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Toolbar
        Panel toolbar = new Panel();
        toolbar.setLayout(new FlowLayout());

        Button rectButton = createStyledButton("Rectangle", new Color(0, 120, 215)); // Rectangle Button
        Button polyButton = createStyledButton("Polygon", new Color(220, 50, 50)); // Polygon Button

        new DragSource().createDefaultDragGestureRecognizer(rectButton, DnDConstants.ACTION_COPY, new DragGestureHandler("Rectangle")); // Drag Gesture Recognizer for Rectangle
        new DragSource().createDefaultDragGestureRecognizer(polyButton, DnDConstants.ACTION_COPY, new DragGestureHandler("Polygon")); // Drag Gesture Recognizer for Polygon

        toolbar.add(rectButton);
        toolbar.add(polyButton);
        add(toolbar, BorderLayout.NORTH);

        // Drawing Canvas with DropTarget
        drawingCanvas = new Canvas() {
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
        drawingCanvas.setBackground(Color.WHITE);
        drawingCanvas.setPreferredSize(new Dimension(900, 600));
        new DropTarget(drawingCanvas, new ShapeDropTargetListener());

        add(drawingCanvas, BorderLayout.CENTER); // Adding the canvas to the frame
        setVisible(true); // Making the application visible
    }

    private Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addShape(AbstractForm shape) {
        shapes.add(shape);
        drawingCanvas.repaint();
    }

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

    // Class DragGestureHandler to handle the Drag
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

    // Transferable class to handle the Transfer
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

    // Class ShapeDropTargetListener to handle the Drop
    private class ShapeDropTargetListener extends DropTargetAdapter {
        @Override
        public void drop(DropTargetDropEvent dtde) {
            try {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = dtde.getTransferable();
                DataFlavor[] flavors = transferable.getTransferDataFlavors();

                for (DataFlavor flavor : flavors) {
                    if (flavor.equals(DataFlavor.stringFlavor)) {
                        String shapeType = (String) transferable.getTransferData(flavor);
                        Point dropPoint = dtde.getLocation();
                        if (shapeType.equals("Rectangle")) {
                            addShape(new Rectangle(dropPoint.x, dropPoint.y, 120, 60, 0, 0, 255));
                        } else if (shapeType.equals("Polygon")) {
                            addShape(new RegularPolygon(dropPoint.x, dropPoint.y, 6, 50, 255, 0, 0));
                        }
                        dtde.dropComplete(true);
                        return;
                    }
                }
                dtde.rejectDrop();
            } catch (UnsupportedFlavorException | IOException ex) {
                dtde.rejectDrop();
            }
        }
    }

    public static void main(String[] args) {
        new DrawingApp();
    }
}