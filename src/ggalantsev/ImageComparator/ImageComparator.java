package ggalantsev.ImageComparator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.*;
import java.util.logging.Formatter;


public class ImageComparator {

    // Logger
    private static final Logger LOGGER = Logger.getLogger(ImageComparator.class.getName());
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    static {
        Handler handlerObj = new ConsoleHandler();
        handlerObj.setLevel(Level.ALL);
        handlerObj.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder builder = new StringBuilder(1000);
                builder.append(df.format(new Date(record.getMillis()))).append(" ");
//                builder.append("[").append(record.getSourceMethodName()).append("] - ");
                builder.append("[").append(record.getLevel()).append("] - ");
                builder.append(formatMessage(record));
                builder.append("\n");
                return builder.toString();
            }
        });
        LOGGER.addHandler(handlerObj);
        LOGGER.setLevel(Level.ALL);
        LOGGER.setUseParentHandlers(false);
    }

    // ImageComparator fields
    private BufferedImage img1 = null;
    private BufferedImage img2 = null;
    private boolean firstIsMain; // Selecting image for drawing rectangles

    private double deviation = 0.2; // color deviation between images
    private int distance = 25; // distance to near point for adding in rectangle

    public ImageComparator(String img1, String img2, boolean firstIsMain) {
        this.img1 = readImageFromFile(img1);
        this.img2 = readImageFromFile(img2);
        this.firstIsMain = firstIsMain;
        LOGGER.info("ImageComparator initialized.");
    }

    public BufferedImage compare() throws NullPointerException {
        LOGGER.info("Comparing started...");
        BufferedImage mainImg;
        if (firstIsMain) {
            mainImg = this.img1;
        } else {
            mainImg = this.img2;
        }

        List<Point> differentPoints = getDifferentPoints();

        LOGGER.info("Deviation: " + deviation + ". Distance: " + distance + ".");
        LOGGER.info("Number of Different Points: " + differentPoints.size() + ".");

        return drawRectangles(
                getRectangles(differentPoints),
                mainImg);
    }

    private List<Point> getDifferentPoints() {
        boolean imagesHaveDifferentResolution = false;
        List<Point> list = new LinkedList<>();
        for (int i = 0; i < img1.getWidth(); i++) {
            for (int j = 0; j < img1.getHeight(); j++) {
                try {
                    if (isColorDifferent(img1.getRGB(i, j), img2.getRGB(i, j))) {
                        list.add(new Point(i, j));
                    }
                } catch (IndexOutOfBoundsException e) {
                    imagesHaveDifferentResolution = true;
                }
            }
        }

        if(imagesHaveDifferentResolution) LOGGER.warning("Images have different resolutions");

        return list;
    }

    private BufferedImage drawRectangles(List<Rectangle> rectangles, BufferedImage img) {
        for (Rectangle rectangle : rectangles) {
            img = drawRectangle(rectangle, img);
        }
        return img;
    }

    private BufferedImage drawRectangle(Rectangle rectangle, BufferedImage image){
        Graphics2D g2d = image.createGraphics();
        int thickness = 2;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(thickness));
        g2d.setColor(Color.RED);
        g2d.drawRect(
                rectangle.getMin().getX(),
                rectangle.getMin().getY(),
                rectangle.getWidth(),
                rectangle.getHeight());
        g2d.setStroke(oldStroke);

        return image;
    }

    private List<Rectangle> getRectangles(List<Point> differentPoints) {
        List<Rectangle> rectangles = new ArrayList<>();
        while (!differentPoints.isEmpty()) {
            List<Point> area = new ArrayList<>();
            Point point = differentPoints.get(0);
            collectNearPoints(differentPoints, area, point);
            rectangles.add(new Rectangle(area));
            area.clear();
        }
        LOGGER.info("Images have " + rectangles.size() + " different areas.");
        return rectangles;
    }

    private void collectNearPoints(List<Point> differentPoints, List<Point> area, Point point) {
        if (differentPoints.contains(point)) {
            area.add(point);
            differentPoints.remove(point);

            boolean newPointsAdded = true;
            Rectangle rectangle = new Rectangle(point, point);

            while (newPointsAdded) { // extend rectangle while no new different points near
                newPointsAdded = false;
                for (Iterator<Point> iter = differentPoints.iterator(); iter.hasNext(); ) {
                    Point next = iter.next();
                    if (isPointNearRectangle(next, rectangle, distance)) {
                        area.add(next);
                        iter.remove();
                        newPointsAdded = true;
                    }
                }
                rectangle = new Rectangle(area);
            }
            LOGGER.info("All points: " + differentPoints.size() + ".\tArea points: " + area.size() + "." + "\nRectangle: " + rectangle.toString() + ".");
        }
        return;
    }

    private boolean isPointNearRectangle(Point point, Rectangle rectangle, int d) {
        if (point.getX() >= rectangle.getMin().getX() - d &&
                point.getY() >= rectangle.getMin().getY() - d &&
                point.getX() <= rectangle.getMax().getX() + d &&
                point.getY() <= rectangle.getMax().getY() + d) {
            return true;
        } else {
            return false;
        }
    }

    private BufferedImage readImageFromFile(String imgName) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imgName));
        } catch (IOException e) {
            LOGGER.warning("Can't read image \"" + imgName + "\" from file!");
        }
        return img;
    }

    private boolean isColorDifferent(int color1, int color2) {
        int c1r = (color1 & 0x00ff0000) >> 4 * 4; // red channel
        int c1g = (color1 & 0x0000ff00) >> 2 * 4; // green channel
        int c1b = (color1 & 0x000000ff); // blue channel
        int c2r = (color2 & 0x00ff0000) >> 4 * 4;
        int c2g = (color2 & 0x0000ff00) >> 2 * 4;
        int c2b = (color2 & 0x000000ff);

        double colorDifference = Math.sqrt(
                Math.pow(c1r - c2r, 2)
                        + Math.pow(c1g - c2g, 2)
                        + Math.pow(c1b - c2b, 2));

        double blackWhiteDifference = Math.sqrt(
                Math.pow(255, 2)
                        + Math.pow(255, 2)
                        + Math.pow(255, 2));

        if ((colorDifference / blackWhiteDifference) < this.deviation) {
            return false;
        } else {
            return true;
        }
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public static void writeImageToFile(BufferedImage img, String output) {
        try {
            ImageIO.write(img, "PNG", new File(output));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
