package com.lf.advent.util;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.math3.complex.Complex;

import java.util.Set;

@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@Getter
public class Point {
    public static final Point ZERO = Point.of(0, 0);

    public int x;
    public int y;

    public static Point of(int x, int y) {
        return Point.builder()
                    .x(x)
                    .y(y)
                    .build();
    }

    public int distance1(Point other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    public int module1() {
        return distance1(ZERO);
    }

    public double distance2(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2d) + Math.pow(y - other.y, 2d));
    }

    public double module2() {
        return distance2(ZERO);
    }

    public double argument(Point origin) {
        Point t = this.translate(origin);
        if (t.x == 0 && t.y < 0) {
            return 0d;
        }
        Complex complex = new Complex(t.x, -t.y);
        double argument = complex.getArgument() - Math.PI / 2d;
        if (argument < 0) {
            argument += 2d * Math.PI;
        }
        return 2d * Math.PI - argument;
    }

    public Point translate(Point origin) {
        return Point.of(this.x - origin.x, this.y - origin.y);
    }

    public long findVisible(Set<Point> asteroids) {
        return asteroids.stream()
                        .filter(point -> !this.equals(point))
                        .filter(point1 -> canSee(point1, asteroids))
                        .count();
    }

    public boolean canSee(Point point, Set<Point> asteroids) {
        if (point.x < this.x) {
            return point.canSee(this, asteroids);
        }

        double gDistance = this.distance2(point);
        return asteroids.stream()
                        .filter(point1 -> !point.equals(point1))
                        .filter(point1 -> !this.equals(point1))
                        .noneMatch(point1 -> (point1.distance2(this) + point1.distance2(point) - gDistance) <= 1e-10);

    }

    public int alignment() {
        return x * y;
    }

}