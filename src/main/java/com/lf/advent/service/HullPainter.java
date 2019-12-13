package com.lf.advent.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
public class HullPainter implements LinesConsumer {

    Map<Point, Color> hull = Maps.mutable.empty();
    Set<Point> painted = Sets.mutable.empty();

    @Override
    public void consume(List<String> lines) {
        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        IntCode.Instruction signal = null;

        Robot robot = new Robot();
        hull.put(robot.spot(), Color.WHITE);

        while (signal != IntCode.Instruction.FINISH) {
            service.getIo().input.add((long) hull.getOrDefault(robot.spot(), Color.BLACK).getCode());
            signal = service.execute();
            signal = service.execute();

            long colorCode = service.getIo().output.get(service.getIo().output.size() - 2);
            long directionCode = service.getIo().output.get(service.getIo().output.size() - 1);

            Color color = Color.of((int) colorCode);
            Direction direction = Direction.of((int) directionCode);

//            log.info("Paint in {} and {}", color, direction);
            painted.add(robot.spot());

            robot.paint(color, hull);
            robot.move(direction);
        }

        log.info("number of painted panel = {}", hull.size());
        log.info("number of painted panel = {}", painted.size());

        IntSummaryStatistics xSummaryStatistics = painted.stream()
                                                         .mapToInt(painted -> painted.x)
                                                         .summaryStatistics();
        IntSummaryStatistics ySummaryStatistics = painted.stream()
                                                         .mapToInt(painted -> painted.y)
                                                         .summaryStatistics();

        log.info("x = {}", xSummaryStatistics);
        log.info("y = {}", ySummaryStatistics);

        for (int y = ySummaryStatistics.getMax(); y > ySummaryStatistics.getMin() - 1; y--) {
            for (int x = xSummaryStatistics.getMin(); x < xSummaryStatistics.getMax() + 1; x++) {
                Point point = Point.of(x, y);
                Color color = hull.getOrDefault(point, Color.BLACK);
                String s = color == Color.BLACK ? " " : "#";
                System.out.print(s);
            }
            System.out.println();
        }
    }

    public static class Robot {
        int x = 0;
        int y = 0;
        Position position = Position.UP;

        public void paint(Color color, Map<Point, Color> hull) {
            hull.put(spot(), color);
        }

        public void move(Direction direction) {
            direction.turn(this);
        }

        public Point spot() {
            return Point.of(x, y);
        }
    }

    enum Position {
        UP(robot -> {
            robot.x = robot.x - 1;
        }, robot -> {
            robot.x = robot.x + 1;
        }),
        LEFT(robot -> {
            robot.y = robot.y - 1;
        }, robot -> {
            robot.y = robot.y + 1;
        }),
        DOWN(robot -> {
            robot.x = robot.x + 1;
        }, robot -> {
            robot.x = robot.x - 1;
        }),
        RIGHT(robot -> {
            robot.y = robot.y + 1;
        }, robot -> {
            robot.y = robot.y - 1;
        });

        private Consumer<Robot> turnLeft;
        private Consumer<Robot> turnRight;

        Position(Consumer<Robot> turnLeft, Consumer<Robot> turnRight) {
            this.turnLeft = turnLeft;
            this.turnRight = turnRight;
        }

        public void turnLeft(Robot robot) {
            this.turnLeft.accept(robot);
        }

        public void turnRight(Robot robot) {
            this.turnRight.accept(robot);
        }
    }

    enum Direction {
        TURN_LEFT(0,
                  position -> Position.values()[(position.ordinal() + 1) % 4],
                  robot -> robot.position.turnLeft(robot)),
        TURN_RIGHT(1,
                   position -> Position.values()[(position.ordinal() + 3) % 4],
                   robot -> robot.position.turnRight(robot));

        private int code;
        private Function<Position, Position> turn;
        private Consumer<Robot> translate;

        Direction(int code, Function<Position, Position> turn, Consumer<Robot> translate) {
            this.code = code;
            this.turn = turn;
            this.translate = translate;
        }

        public static Direction of(int code) {
            return Arrays.stream(values())
                         .filter(direction -> direction.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Direction " + code + " is not recognised."));
        }

        public void turn(Robot robot) {
            this.translate.accept(robot);
            robot.position = this.turn.apply(robot.position);
        }
    }

    enum Color {
        BLACK(0),
        WHITE(1);

        @Getter
        private int code;

        Color(int code) {
            this.code = code;
        }

        public static Color of(int code) {
            return Arrays.stream(values())
                         .filter(color -> color.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Color " + code + " is not recognised."));
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
