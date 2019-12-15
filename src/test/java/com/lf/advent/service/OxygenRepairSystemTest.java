package com.lf.advent.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OxygenRepairSystemTest {

    @Test
    public void name() {
        List<String> lines = Lists.mutable.of(" ##### ##### ### ############# ######### ",
                "#.....#.....#...#.............#.........#",
                " ##.#.###.#.#.#.#.#####.#####.###.#####.#",
                "#...#...#.#...#.#.#...#.#.....#...#.....#",
                "#.#####.#.#.#####.#.#.#.#.#####.###.#### ",
                "#.#...#...#.#...#...#.#.#.....#...#.#...#",
                "#.#.#.#######.#.#####.#.#####.###.#.###.#",
                "#...#.........#.......#.....#.....#...#.#",
                "#.## ######################.###### ##.#.#",
                "#...#.....#.....#.#.......#...#...#...#.#",
                " ##.#.###.#.###.#.#.#####.###.#.#.#.###.#",
                "#...#...#...#.#...#.#...#...#.#.#...#...#",
                "#.#####.#####.###.#.###.###.#.#.#######.#",
                "#.......#...#.....#...#.......#...#.....#",
                " ########.#.#.#### ##.###.#######.###.## ",
                "#...#.....#.#.#...#.#...#.#.....#...#...#",
                "#.###.#####.#.#.#.#.###.###.###.###.###.#",
                "#.....#.#...#...#.....#.....#.#.#.......#",
                "#.#####.#.###########.#######.#.#######.#",
                "#.#.....#.........#.......#...#.....#...#",
                "#.#.###.#####.#.###.#####.###.#####.#### ",
                "#...#...#...#.#.#...#...#.........#.#...#",
                " ####.###.#.#.###.#####.###.#######.#.#.#",
                "#.....#...#.#.........#.#...#.......#.#.#",
                "#.#####.###.###########.#.###.#####.#.#.#",
                "#...#...#.#.............#.#...#.....#.#.#",
                " ##.#.###.#################.#.#######.#.#",
                "#.#.#...#...#.......#.....#.#.#.....#.#.#",
                "#.#.###.#.#.#.#####.###.#.#.#.#.###.#.#.#",
                "#...#...#.#...#...#.#...#...#.#...#...#.#",
                "#.###.###.#####.###.#.###########.#####.#",
                "#...#.#.....#...#...#.............#...#.#",
                " ##.#.#####.#.#.#.###.#########.###.#.#.#",
                "#...#.....#.#.#.#.#...#.......#.....#O#.#",
                "#.#######.#.#.#.#.#####.#####.###### ##.#",
                "#.....#.#...#.#...#.....#...#...#...#...#",
                " ####.#.#####.#####.#####.#.###.#.#.#.## ",
                "#...#.....#...#...#.....#.#...#...#.#...#",
                "#.#.#####.#.###.#.#####.#.###.#####.###.#",
                "#.#.......#.....#.......#...#...........#",
                " # ####### ##### ####### ### ########### ");

        Map<Point, Tile> map = Maps.mutable.empty();
        parseInput(lines, map);

        Point oxygen = retrieveOxygen(map);

        int reach = allReach(oxygen, map) - 1;

        System.out.println("reach = " + reach);

    }

    public int allReach(Point start, Map<Point, Tile> map) {
        Set<Point> visited = Sets.mutable.of(start);
        int previousSize = 0;
        int count = 0;
        while (previousSize != visited.size()) {
            previousSize = visited.size();
            Set<Point> adjacent = visited.stream()
                                         .map(point -> Sets.mutable.of(Direction.EAST.computeNextPoint(point),
                                                 Direction.WEST.computeNextPoint(point),
                                                 Direction.NORTH.computeNextPoint(point),
                                                 Direction.SOUTH.computeNextPoint(point)))
                                         .flatMap(Collection::stream)
                                         .filter(point -> !visited.contains(point))
                                         .filter(map::containsKey)
                                         .filter(point -> map.get(point) != Tile.WALL)
                                         .collect(Collectors.toSet());
            visited.addAll(adjacent);
            count++;
        }
        return count;
    }

    public Point retrieveOxygen(Map<Point, Tile> map) {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue().equals(Tile.OXYGEN))
                  .map(Map.Entry::getKey)
                  .findAny()
                  .orElseThrow(() -> new IllegalStateException("Droid not found."));
    }

    public void parseInput(List<String> lines, Map<Point, Tile> map) {
        for (int y = 0 ; y < lines.size() ; y++) {
            String line = lines.get(y);
            String[] t = line.split("");
            for (int x = 0 ; x < line.length() ; x++) {
                if (" ".equals(t[x])) {
                    continue;
                }

                map.put(Point.of(x, y), Tile.of(t[x]));
            }
        }
    }

    public enum Tile {
        OXYGEN(0, "O"),
        WALL(2, "#"),
        EMPTY(3, ".");

        private int code;
        @Getter
        private String display;

        Tile(int code, String display) {
            this.code = code;
            this.display = display;
        }

        public static Tile of(int code) {
            return Arrays.stream(values())
                         .filter(tileId -> tileId.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Tile " + code + " is not recognised."));
        }

        public static Tile of(String display) {
            return Arrays.stream(values())
                         .filter(tileId -> tileId.display.equals(display))
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Tile " + display + " is not recognised."));
        }
    }

    @Getter
    public enum Direction {
        SOUTH(2, point -> point.toBuilder().y(point.y - 1).build()),
        EAST(4, point -> point.toBuilder().x(point.x + 1).build()),
        NORTH(1, point -> point.toBuilder().y(point.y + 1).build()),
        WEST(3, point -> point.toBuilder().x(point.x - 1).build());

        private long code;
        private Function<Point, Point> nextPointComputer;

        Direction(long code, Function<Point, Point> nextPointComputer) {
            this.code = code;
            this.nextPointComputer = nextPointComputer;
        }

        public static Direction of(long code) {
            return Arrays.stream(values())
                         .filter(direction -> direction.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Direction " + code + " is not recognised."));
        }

        public Point computeNextPoint(Point currentPosition) {
            return this.nextPointComputer.apply(currentPosition);
        }
    }

    @Builder(toBuilder = true)
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