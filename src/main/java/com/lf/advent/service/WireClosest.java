package com.lf.advent.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Getter
public class WireClosest implements LinesConsumer {

    private int closestIntersection;
    private int minStep;

    @Override
    public void consume(List<String> lines) {
        List<List<Point>> wires = lines.stream()
                                       .map(line -> line.split(","))
                                       .map(instructions -> buildPath(Lists.fixedSize.of(instructions)))
                                       .collect(Collectors.toList());

        List<Point> intersections = ListUtils.intersection(wires.get(0), wires.get(1));
        closestIntersection = intersections.stream()
                                           .mapToInt(Point::module)
                                           .min()
                                           .orElse(0);

        log.info("Closest intersection distance is = {}", closestIntersection);

        minStep = intersections.stream()
                               .mapToInt(intersection -> wires.get(0).indexOf(intersection) + wires.get(1).indexOf(intersection) + 2)
                               .min()
                               .orElse(0);

        log.info("Min step = {}", minStep);
    }

    public List<Point> buildPath(List<String> instructions) {
        List<Pair<Direction, Integer>> directions = instructions.stream()
                                                                .map(instruction -> Tuples.pair(Direction.of(instruction.substring(0, 1)), Integer.parseInt(instruction.substring(1))))
                                                                .collect(Collectors.toList());
        MutableList<Point> path = Lists.mutable.empty();
        Point start = Point.ZERO;

        for (Pair<Direction, Integer> direction : directions) {
            path.addAll(direction.getOne().computePath(start, direction.getTwo()));
            start = path.getLast();
        }

        return path;
    }

    enum Direction {
        UP("U", (start, distance) -> Point.of(start.x, start.y + distance)),
        DOWN("D", (start, distance) -> Point.of(start.x, start.y - distance)),
        RIGHT("R", (start, distance) -> Point.of(start.x + distance, start.y)),
        LEFT("L", (start, distance) -> Point.of(start.x - distance, start.y));

        private String code;
        private BiFunction<Point, Integer, Point> pointComputer;

        Direction(String code, BiFunction<Point, Integer, Point> pointComputer) {
            this.code = code;
            this.pointComputer = pointComputer;
        }

        public static Direction of(String code) {
            return Arrays.stream(values())
                         .filter(direction -> direction.code.equals(code))
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Direction " + code + " is not recognised."));
        }

        public List<Point> computePath(Point start, int distance) {
            return IntStream.range(1, distance + 1)
                            .mapToObj(y -> this.pointComputer.apply(start, y))
                            .collect(Collectors.toList());
        }
    }

    @Builder
    @ToString
    @EqualsAndHashCode
    public static class Point {
        public static final Point ZERO = Point.of(0, 0);

        private int x;
        private int y;

        public static Point of(int x, int y) {
            return Point.builder()
                        .x(x)
                        .y(y)
                        .build();
        }

        public int distance(Point other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

        public int module() {
            return distance(ZERO);
        }
    }
}
