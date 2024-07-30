package coalitionofpowers.UI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelGrabber;
import java.io.IOException;

import javax.swing.JPanel;

import coalitionofpowers.Controllers.MapController;

public class MapView extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

    private final BufferedImage claimsImage;
    private final BufferedImage terrainImage;
    private final BufferedImage regionsImage;
    private final BufferedImage occupationsImage;
    private final BufferedImage devastationImage;

    private final int[] claimsImagePixels;
    private final int[] terrainImagePixels;
    private final int[] regionsImagePixels;
    private final int[] occupationsImagePixels;
    private final int[] devastationImagePixels;

    private double zoomFactor = 1;
    private final double minZoom = 0.1;
    private final double maxZoom = 20;
    private final double zoomIncrement = 0.2;
    private double prevZoomFactor = 1;

    private boolean dragging;
    private boolean zooming;
    private boolean released;

    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;

    private final MapController mapController;

    public MapView(BufferedImage claimImage, BufferedImage terrainImage, BufferedImage regionImage,
            BufferedImage occupationsImage, BufferedImage devastationImage, MapController mapController) throws IOException, InterruptedException {

        //Storing copies of the images' pixel arrays for later accesses
        claimsImagePixels = getPixels(claimImage);
        terrainImagePixels = getPixels(terrainImage);
        regionsImagePixels = getPixels(regionImage);
        occupationsImagePixels = getPixels(occupationsImage);
        devastationImagePixels = getPixels(devastationImage);

        //toCompatibleImage used so that the BufferedImages later used in rendering are:
        //1: using compatible models (supposedly a good pratice, haven't actually check lol)
        //2: not being kept in RAM instead of VRAM after having their buffer accessed in getPixels (VERY big performance issue)
        this.claimsImage = toCompatibleImage(claimImage);
        this.terrainImage = toCompatibleImage(terrainImage);
        this.regionsImage = toCompatibleImage(regionImage);
        this.occupationsImage = toCompatibleImage(occupationsImage);
        this.devastationImage = toCompatibleImage(devastationImage);

        this.mapController = mapController;

        initComponent();
    }

    private BufferedImage toCompatibleImage(BufferedImage image) {
        //Taken from https://stackoverflow.com/questions/196890/java2d-performance-issues

        GraphicsConfiguration gfxConfig = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        if (image.getColorModel().equals(gfxConfig.getColorModel())) {
            return image;
        }

        BufferedImage newImage = gfxConfig.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        Graphics2D g2d = newImage.createGraphics();

        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return newImage;
    }

    private void initComponent() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    public int[] getClaimImagePixels() throws InterruptedException {
        return claimsImagePixels;
    }

    public int[] getTerrainImagePixels() throws InterruptedException {
        return terrainImagePixels;
    }

    public int[] getRegionsImagePixels() throws InterruptedException {
        return regionsImagePixels;
    }

    public int[] getOccupationsImagePixels() throws InterruptedException {
        return occupationsImagePixels;
    }

    public int[] getDevastationImagePixels() throws InterruptedException {
        return devastationImagePixels;
    }

    private int[] getPixels(BufferedImage image) throws InterruptedException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];

        byte[] bytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        //Check if image bit depth is 8bit or 24bit. Big performance hit when processing 24bit png instead of 8bit (960ms vs 40ms) with PixelGrabber
        if (bytes.length != pixels.length) {
            //Handle a 24bit image
            int alphaOffset = image.getAlphaRaster() != null ? 1 : 0;

            for (int i = 0; i + 2 + alphaOffset < pixels.length; i++) {
                pixels[i] += ((int) bytes[i * 3 + alphaOffset] & 0xff); // blue
                pixels[i] += (((int) bytes[i * 3 + alphaOffset + 1] & 0xff) << 8); // green
                pixels[i] += (((int) bytes[i * 3 + alphaOffset + 2] & 0xff) << 16); // red
            }
        } else {
            //Handle a 8bit image
            new PixelGrabber(image, 0, 0, width, height, pixels, 0, width).grabPixels();
        }

        return pixels;
    }

    public int getMapHeight() {
        return claimsImage.getHeight();
    }

    public int getMapWidth() {
        return claimsImage.getWidth();
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
        g2.drawImage(claimsImage, 0, 0, this);
        g2.drawImage(occupationsImage, 0, 0, this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zooming = true;

        int numRotation = e.getWheelRotation();

        //Zoom in
        if (numRotation < 0 && zoomFactor < maxZoom) {
            zoomFactor += zoomFactor * zoomIncrement;
            if (zoomFactor > maxZoom) {
                zoomFactor = maxZoom;
            }
            repaint();
        }
        //Zoom out
        if (numRotation > 0 && zoomFactor > minZoom) {
            zoomFactor -= zoomFactor * zoomIncrement;
            if (zoomFactor < minZoom) {
                zoomFactor = minZoom;
            }
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

        int index = mouseY * getMapWidth() + mouseX;

        int claimColor = claimsImagePixels[index] & 0xffffff;
        int terrainColor = terrainImagePixels[index] & 0xffffff;
        int regionColor = regionsImagePixels[index] & 0xffffff;
        int occupationColor = occupationsImagePixels[index] & 0xffffff;
        int devastationColor = devastationImagePixels[index] & 0xffffff;

        mapController.showInfoForPixel(claimColor, terrainColor, regionColor, occupationColor, devastationColor);
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
