package ggalantsev.Comparator;

import java.util.ArrayList;
import java.util.List;

public class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public List<Point> getNearPoints() {
        List<Point> list = new ArrayList<>();
        list.add(new Point(this.x - 1, this.y));
        list.add(new Point(this.x + 1, this.y));
        list.add(new Point(this.x, this.y - 1));
        list.add(new Point(this.x, this.y + 1));
        list.add(new Point(this.x + 1, this.y + 1));
        list.add(new Point(this.x + 1, this.y - 1));
        list.add(new Point(this.x - 1, this.y + 1));
        list.add(new Point(this.x - 1, this.y - 1));

//        list.add(new Point(this.x, this.y + 2));
//        list.add(new Point(this.x, this.y - 2));
//        list.add(new Point(this.x + 2, this.y));
//        list.add(new Point(this.x - 2, this.y));

        return list;
    }

    public double getDistanceX(Point point) {
        return (this.getX() - point.getX());
    }

    public double getDistanceY(Point point) {
        return (this.getY() - point.getY());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        Point point = (Point) o;
        return ((this.x == point.getX()) && (this.y == point.getY()));
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
