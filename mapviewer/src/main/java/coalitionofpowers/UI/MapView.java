package coalitionofpowers.UI;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import coalitionofpowers.Controllers.MapController;
import coalitionofpowers.Model.Claim;

public class MapView extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

    private final BufferedImage baseImage;
    private final BufferedImage terrainImage;

    private double zoomFactor = 1;
    private final double minZoom = 0.25;
    private final double maxZoom = 5;
    private double prevZoomFactor = 1;

    private boolean dragging;
    private boolean zooming;
    private boolean released;

    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;

    private MapController mapController;

    public MapView(BufferedImage baseImageFilepath, BufferedImage terrainImageFilepath, MapController mapController) throws IOException {
        baseImage = baseImageFilepath;
        terrainImage = terrainImageFilepath;
        this.mapController = mapController;

        initComponent();
    }

    private void initComponent() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public int[] getBaseImagePixels() throws InterruptedException {
        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        int[] pixels = new int[width * height];

        new PixelGrabber(baseImage, 0, 0, width, height, pixels, 0, width).grabPixels();

        return pixels;
    }

    public int[] getTerrainImagePixels() throws InterruptedException {
        int width = terrainImage.getWidth();
        int height = terrainImage.getHeight();
        int[] pixels = new int[width * height];

        new PixelGrabber(terrainImage, 0, 0, width, height, pixels, 0, width).grabPixels();

        return pixels;
    }

    public int getMapHeight() {
        return baseImage.getHeight();
    }

    public int getMapWidth() {
        return baseImage.getWidth();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        double xOffsetFinal = xOffset;
        double yOffsetFinal = yOffset;

        if (zooming) {
            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

            double zoomDiv = zoomFactor / prevZoomFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            xOffsetFinal = xOffset;
            yOffsetFinal = yOffset;

            prevZoomFactor = zoomFactor;

            zooming = false;
        }

        if (dragging) {
            xOffsetFinal = xOffset + xDiff;
            yOffsetFinal = yOffset + yDiff;

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragging = false;
            }
        }

        AffineTransform at = new AffineTransform();
        at.translate(xOffsetFinal, yOffsetFinal);
        at.scale(zoomFactor, zoomFactor);
        g2.transform(at);

        // All drawings go here
        g2.drawImage(terrainImage, 0, 0, this);
        g2.drawImage(baseImage, 0, 0, this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zooming = true;

        int numRotation = e.getWheelRotation();

        //Zoom in
        if (numRotation < 0 && zoomFactor < maxZoom) {
            zoomFactor += 0.25;
            repaint();
        }
        //Zoom out
        if (numRotation > 0 && zoomFactor > minZoom) {
            zoomFactor -= 0.25;
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;

        dragging = true;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = (int) ((e.getX() - xOffset) / zoomFactor);
        int mouseY = (int) ((e.getY() - yOffset) / zoomFactor);

        int claimColor = baseImage.getRGB(mouseX, mouseY);
        baseImage.setRGB(mouseX, mouseY, claimColor);

        mapController.showInfoForClaim(claimColor & 0x0000000000ffffff, e.getPoint());
    }

    public void showInfoForClaim(Claim claim, Point point) {
        JTextArea textArea;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        textArea = new JTextArea("Claim: " + claim.name + "\n Tax: " + claim.totalTax + "\n Manpower: " + claim.totalManpower);

        textArea.setEditable(false);
        panel.add(textArea, BorderLayout.CENTER);

        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setLocation(point);
        frame.setVisible(true);

        frame.setSize(frame.getWidth() * 3 / 2, frame.getHeight());
        frame.setAlwaysOnTop(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        released = false;
        startPoint = MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        released = true;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
