package draw.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import draw.core.AbstractForm;
import draw.core.Rectangle;
import draw.core.RegularPolygon;

public class DrawingApp extends JFrame {
    private final List<AbstractForm> shapes = new ArrayList<>();
    private final JPanel drawingPanel;

    public DrawingApp() {
        setTitle("Drawing App");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Barre d'outils modernisée
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton rectButton = createStyledButton("Rectangle", new Color(0, 120, 215));
        JButton polyButton = createStyledButton("Polygon", new Color(220, 50, 50));

        rectButton.setTransferHandler(new ShapeTransferHandler("Rectangle"));
        polyButton.setTransferHandler(new ShapeTransferHandler("Polygon"));

        rectButton.addMouseListener(new DragMouseAdapter(rectButton));
        polyButton.addMouseListener(new DragMouseAdapter(polyButton));

        toolbar.add(rectButton);
        toolbar.add(polyButton);
        add(toolbar, BorderLayout.NORTH);

        // Panneau de dessin avec DropTarget
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
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
        drawingPanel.setBackground(Color.WHITE);
        new DropTarget(drawingPanel, new ShapeDropTargetListener());
        add(drawingPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addShape(AbstractForm shape) {
        shapes.add(shape);
        drawingPanel.repaint();
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

    private static class DragMouseAdapter extends MouseAdapter {
        private final JComponent component;

        public DragMouseAdapter(JComponent component) {
            this.component = component;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            TransferHandler handler = component.getTransferHandler();
            handler.exportAsDrag(component, e, TransferHandler.COPY);
        }
    }

    private static class ShapeTransferHandler extends TransferHandler {
        private final String shapeType;

        public ShapeTransferHandler(String shapeType) {
            this.shapeType = shapeType;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return new StringSelection(shapeType);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY;
        }
    }

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
        SwingUtilities.invokeLater(() -> new DrawingApp().setVisible(true));
    }
}