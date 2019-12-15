package com.lf.advent.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OxygenRepairSystem implements LinesConsumer {

    private Map<Point, Tile> map = Maps.mutable.empty();
    private int path;

    @Override
    public void consume(List<String> lines) {

        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        IntCode.Instruction signal = null;

        MyInput input = new MyInput();
        service.getIo().input = input;
        List<Long> output = service.getIo().output;
        Point droid = Point.ZERO.toBuilder().build();
        map.put(droid, Tile.DROID);
        Point oxygen = null;
        input.setMap(map);

        Deque<Direction> directionsTaken = new ArrayDeque<>();

        while (signal != IntCode.Instruction.FINISH) {
            signal = service.execute();
            Output result = Output.of(output.get(output.size() - 1));

            result.updateMap(input.getLastInput(), map);
            result.updateTakenDirection(input.getLastInput(), directionsTaken);

            if (!input.isHasFoundOxygen() && result == Output.IS_ON_TARGET) {
                oxygen = map.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(Tile.DROID))
                            .map(Map.Entry::getKey)
                            .findAny()
                            .orElseThrow(() -> new IllegalStateException("Droid not found."));
                input.setHasFoundOxygen(true);
                display();
            }

            if (allExplored()) {
                break;
            }
        }

        display();
        log.info("path = {}", directionsTaken.size());
        path = allReach(oxygen) - 1;
        log.info("path = {}", path);
    }

    public boolean allExplored() {
        long count = map.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != Tile.WALL)
                        .map(Map.Entry::getKey)
                        .filter(point -> !map.containsKey(Direction.EAST.computeNextPoint(point)) ||
                                !map.containsKey(Direction.WEST.computeNextPoint(point)) ||
                                !map.containsKey(Direction.NORTH.computeNextPoint(point)) ||
                                !map.containsKey(Direction.SOUTH.computeNextPoint(point)))
                        .count();
        return count == 0;
    }

    public Set<Point> unExplored() {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() != Tile.WALL)
                  .map(Map.Entry::getKey)
                  .filter(point -> !map.containsKey(Direction.EAST.computeNextPoint(point)) ||
                          !map.containsKey(Direction.WEST.computeNextPoint(point)) ||
                          !map.containsKey(Direction.NORTH.computeNextPoint(point)) ||
                          !map.containsKey(Direction.SOUTH.computeNextPoint(point)))
                  .collect(Collectors.toSet());
    }

    public int allReach(Point start) {
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
                                         .filter(point -> map.containsKey(point))
                                         .filter(point -> map.get(point) != Tile.WALL)
                                         .collect(Collectors.toSet());
            visited.addAll(adjacent);
            count++;
        }
        return count;
    }

    public int closestPath() {
        Point droid = map.entrySet()
                         .stream()
                         .filter(entry -> entry.getValue().equals(Tile.DROID))
                         .map(Map.Entry::getKey)
                         .findAny()
                         .orElseThrow(() -> new IllegalStateException("Droid not found."));

        Set<Point> visited = Sets.mutable.of(Point.ZERO);
        int count = 0;

        while (!visited.contains(droid)) {
            Set<Point> adjacent = visited.stream()
                                         .map(point -> Sets.mutable.of(Direction.EAST.computeNextPoint(point),
                                                 Direction.WEST.computeNextPoint(point),
                                                 Direction.NORTH.computeNextPoint(point),
                                                 Direction.SOUTH.computeNextPoint(point)))
                                         .flatMap(Collection::stream)
                                         .filter(point -> !visited.contains(point))
                                         .filter(point -> map.containsKey(point))
                                         .filter(point -> map.get(point) != Tile.WALL)
                                         .collect(Collectors.toSet());
            visited.addAll(adjacent);
            count++;
        }

        return count;
    }

    public void display() {

        IntSummaryStatistics xs = map.keySet()
                                     .stream()
                                     .mapToInt(point -> point.x)
                                     .summaryStatistics();

        IntSummaryStatistics ys = map.keySet()
                                     .stream()
                                     .mapToInt(point -> point.y)
                                     .summaryStatistics();

//        int width = xs.getMax() - xs.getMin() + 1;
//        int height = ys.getMax() - ys.getMin() + 1;
//        log.info("Width = {}, Height = {}", width, height);

//        if (width != 44 || height != 20) {
//            return;
//        }

//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        for (int y = ys.getMax() ; y > ys.getMin() - 1 ; y--) {
            for (int x = xs.getMin() ; x < xs.getMax() + 1 ; x++) {
                Point point = Point.of(x, y);
                Tile tile = map.getOrDefault(point, Tile.EMPTY);
                if (x == 0 && y == 0 && tile.equals(Tile.VISITED)) {
                    System.out.print("X");
                } else {
                    System.out.print(tile.getDisplay());
                }
            }
            System.out.println();
        }
    }

    public enum Output {
        HIT_WALL(0,
                (input, map) -> {
                    Point droid = retrieveDroid(map);
                    map.put(input.computeNextPoint(droid), Tile.WALL);
                },
                (input, directions) -> {

                }),
        HAS_MOVED(1, (input, map) -> {
            Point droid = retrieveDroid(map);
            map.put(droid, Tile.VISITED);
            map.put(input.computeNextPoint(droid), Tile.DROID);
        },
                (input, directions) -> {
                    if (Direction.values()[(input.ordinal() + 2) % 4] == directions.peekLast()) {
                        directions.pollLast();
                    } else {
                        directions.add(input);
                    }
                }),
        IS_ON_TARGET(2, (input, map) -> {
            Point droid = retrieveDroid(map);
            map.put(droid, Tile.VISITED);
            map.put(input.computeNextPoint(droid), Tile.DROID);
        },
                (input, directions) -> {
                    if (Direction.values()[(input.ordinal() + 2 % 4)] == directions.peekLast()) {
                        directions.pollLast();
                    } else {
                        directions.add(input);
                    }
                });

        private long code;
        private BiConsumer<Direction, Map<Point, Tile>> mapUpdator;
        private BiConsumer<Direction, Deque<Direction>> directionsUpdator;

        Output(long code, BiConsumer<Direction, Map<Point, Tile>> mapUpdator, BiConsumer<Direction, Deque<Direction>> directionsUpdator) {
            this.code = code;
            this.mapUpdator = mapUpdator;
            this.directionsUpdator = directionsUpdator;
        }

        public static Output of(long code) {
            return Arrays.stream(values())
                         .filter(output -> output.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Output " + code + " is not recognised."));
        }

        public static Point retrieveDroid(Map<Point, Tile> map) {
            return map.entrySet()
                      .stream()
                      .filter(entry -> entry.getValue().equals(Tile.DROID))
                      .map(Map.Entry::getKey)
                      .findAny()
                      .orElseThrow(() -> new IllegalStateException("Droid not found."));
        }

        public void updateMap(long lastInput, Map<Point, Tile> map) {
            this.mapUpdator.accept(Direction.of(lastInput), map);
        }

        public void updateTakenDirection(long lastInput, Deque<Direction> directionTaken) {
            this.directionsUpdator.accept(Direction.of(lastInput), directionTaken);
        }
    }

    public enum Tile {
        EMPTY(0, " "),
        DROID(1, "D"),
        WALL(2, "#"),
        VISITED(3, ".");

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

    public static class MyInput extends ArrayDeque<Long> {
        private RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        @Getter
        private long lastInput;
        @Setter
        @Getter
        private boolean hasFoundOxygen = false;
        @Setter
        private Map<Point, Tile> map;

        @Override
        public Long pollFirst() {
            if (!hasFoundOxygen) {
                lastInput = randomDataGenerator.nextLong(1L, 4L);
                return lastInput;
            }
            Point droid = map.entrySet()
                             .stream()
                             .filter(entry -> entry.getValue().equals(Tile.DROID))
                             .map(Map.Entry::getKey)
                             .findAny()
                             .orElseThrow(() -> new IllegalStateException("Droid not found."));

//            log.info("Droid = {}", droid);
            Optional<Direction> direction1 = Sets.mutable.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
                                                         .stream()
                                                         .map(direction -> Tuples.pair(direction, direction.computeNextPoint(droid)))
                                                         .filter(pair -> !map.containsKey(pair.getTwo()))
                                                         .findFirst()
                                                         .map(Pair::getOne);

            if (direction1.isPresent()) {
//                log.info("Direction - {}", direction1);
                lastInput = direction1.get().getCode();
                return lastInput;
            }

            List<Point> pathToTarget = unExplored().stream()
                                                   .map(point -> Tuples.pair(point, path(droid, point)))
                                                   .min(Comparator.comparingInt(entry -> entry.getTwo().size()))
                                                   .map(Pair::getTwo)
                                                   .orElseThrow(() -> new IllegalStateException("No more place to explore"));

//            log.info("Target = {}", pathToTarget.get(pathToTarget.size() - 1));
            Point adjacentToDroid = pathToTarget.get(1);
//            log.info("Adjacent = {}", adjacentToDroid);
            Direction direction2 = Sets.mutable.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
                                               .stream()
                                               .map(direction -> Tuples.pair(direction, direction.computeNextPoint(droid)))
                                               .filter(pair -> pair.getTwo().equals(adjacentToDroid))
                                               .map(Pair::getOne)
                                               .findAny()
                                               .orElseThrow(() -> new IllegalStateException("Adjacent " + adjacentToDroid + " is not adjacent to droid " + droid));

//            log.info("Direction = {}", direction2);
            lastInput = direction2.getCode();
            return lastInput;
        }

        public Set<Point> unExplored() {
            return map.entrySet()
                      .stream()
                      .filter(entry -> entry.getValue() != Tile.WALL)
                      .map(Map.Entry::getKey)
                      .filter(point -> !map.containsKey(Direction.EAST.computeNextPoint(point)) ||
                              !map.containsKey(Direction.WEST.computeNextPoint(point)) ||
                              !map.containsKey(Direction.NORTH.computeNextPoint(point)) ||
                              !map.containsKey(Direction.SOUTH.computeNextPoint(point)))
                      .collect(Collectors.toSet());
        }

        public List<Point> path(Point start, Point target) {
            Set<List<Point>> paths = Sets.mutable.empty();
            paths.add(Lists.mutable.of(start));
            Set<Point> visited = Sets.mutable.of(start);

            while (paths.stream().noneMatch(path -> path.contains(target))) {
                Set<List<Point>> aux = Sets.mutable.empty();
                for (List<Point> path : paths) {
                    Point lastVisited = path.get(path.size() - 1);
                    Sets.mutable.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
                                .stream()
                                .map(direction -> direction.computeNextPoint(lastVisited))
                                .filter(point -> !visited.contains(point))
                                .filter(point -> (!map.containsKey(point)) || (map.get(point) != Tile.WALL))
                                .map(point -> {
                                    MutableList<Point> points = Lists.mutable.ofAll(path);
                                    points.add(point);
                                    return points;
                                })
                                .peek(visited::addAll)
                                .forEach(aux::add);
                }

                paths = aux;
            }


            return paths.stream()
                        .filter(path -> path.contains(target))
                        .min(Comparator.comparingInt(List::size))
                        .get();
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
