package ggalantsev;

import javafx.concurrent.Task;
import ggalantsev.ImageComparator.ImageComparator;
import java.awt.image.BufferedImage;

public class CompareImagesTask extends Task<BufferedImage> {

    private String firstImg;
    private String secondImg;
    private double deviation;
    private int distance;
    private boolean firstIsMain;

    public CompareImagesTask(String firstImg, String secondImg, double deviation, int distance, boolean firstIsMain) {
        super();
        this.firstImg = firstImg;
        this.secondImg = secondImg;
        this.deviation = deviation;
        this.distance = distance;
        this.firstIsMain = firstIsMain;
    }

    @Override
    protected BufferedImage call() throws Exception {
        ImageComparator imageComparator = new ImageComparator(firstImg, secondImg, firstIsMain);
        imageComparator.setDeviation(deviation);
        imageComparator.setDistance(distance);

        try {
            return imageComparator.compare();
        } catch (NullPointerException e){
            return null;
        }
    }
}
