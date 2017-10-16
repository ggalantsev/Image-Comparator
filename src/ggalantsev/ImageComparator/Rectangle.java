package ggalantsev.ImageComparator;

import java.util.Comparator;
import java.util.List;

public class Rectangle {

    private Point min;
    private Point max;

    public Rectangle(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    public Rectangle(List<Point> list) {
        this.max = new Point(
                list.stream().map(point -> point.getX()).max(Comparator.comparingInt(o -> o)).get(),
                list.stream().map(point -> point.getY()).max(Comparator.comparingInt(o -> o)).get()
        );
        this.min = new Point(
                list.stream().map(point -> point.getX()).min(Comparator.comparingInt(o -> o)).get(),
                list.stream().map(point -> point.getY()).min(Comparator.comparingInt(o -> o)).get()
        );
    }

    public Point getMin() {
        return min;
    }

    public void setMin(Point min) {
        this.min = min;
    }

    public Point getMax() {
        return max;
    }

    public void setMax(Point max) {
        this.max = max;
    }

    public int getHeight(){
        return Math.abs(this.max.getY()-this.min.getY());
    }

    public int getWidth(){
        return Math.abs(this.max.getX()-this.min.getX());
    }

    @Override
    public String toString() {
        return "min(" + min.getX() + ", " + min.getY() +
                "), max(" + max.getX() + ", " + max.getY() + ')';
    }
}
