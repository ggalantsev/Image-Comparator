package ggalantsev.Comparator;

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
//        System.out.print("min: " + min);
//        System.out.print(" max: " + max);
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

    public Point getCenter() {
        return new Point(min.getX() + (max.getX() - min.getX()) / 2, min.getY() + (max.getY() - min.getY()) / 2);
    }

    public double getRadiusX() {
        return (max.getX() - min.getX());
    }

    public double getRadiusY() {
        return (max.getY() - min.getY());
    }

    @Override
    public String toString() {
        return "min(" + min.getX() + ", " + min.getY() +
                "), max(" + max.getX() + ", " + max.getY() + ')';
    }
}
