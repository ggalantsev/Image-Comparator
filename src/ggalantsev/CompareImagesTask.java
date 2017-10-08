package ggalantsev;

import javafx.concurrent.Task;
import ggalantsev.Comparator.ImageComparator;

import java.awt.image.BufferedImage;


public class CompareImagesTask extends Task<BufferedImage> {

    private String firstImg;
    private String secondImg;
    private double deviation;
    private int distance;
    private BufferedImage image;
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
        new Thread() {
            @Override
            public void run() {
                try {
                    updateProgressBar();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        ImageComparator imageComparator = new ImageComparator(firstImg, secondImg, firstIsMain);
        imageComparator.setDeviation(deviation);
        imageComparator.setDistance(distance);
        image = imageComparator.compare();

        return image;
    }

    private void updateProgressBar() throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            this.updateProgress(i, 100);
            this.updateMessage(i + " %");
            Thread.sleep(20);
        }
    }


}
