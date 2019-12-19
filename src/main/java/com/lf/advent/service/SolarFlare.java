package com.lf.advent.service;

import com.lf.advent.util.Point;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.collector.Collectors2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SolarFlare implements LinesConsumer {


    public static final int Y_MAX = 60;
    public static final int X_MAX = 84;

    private Map<Point, Tile> map = Maps.mutable.withInitialCapacity((Y_MAX + 1) * (X_MAX + 1));

    public static final String MAIN_ROUTINE = "A,B,A,B,C,C,B,A,B,C";
    public static final String ROUTINE_A = "L,10,R,10,L,10,L,10";
    public static final String ROUTINE_B = "R,10,R,12,L,12";
    public static final String ROUTINE_C = "R,12,L,12,R,6";

    @Override
    public void consume(List<String> lines) {
        part1(lines);

        part2(lines);
    }

    private void part2(List<String> lines) {
        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        memory[0] = 2;

        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        IntCode.Instruction signal = null;
        List<Long> output = service.getIo().output;
        Deque<Long> input = service.getIo().input;
        initializiInput(input);

        while (signal != IntCode.Instruction.FINISH) {
            signal = service.execute();
            Long aLong = output.get(output.size() - 1);
            char tile = (char) aLong.intValue();
            System.out.print(tile);
        }
        log.info("Final output = {}", output.get(output.size() - 1));
    }

    private void initializiInput(Deque<Long> input) {
        input.addAll(MAIN_ROUTINE.chars()
                                 .mapToLong(i -> (long) i)
                                 .boxed()
                                 .collect(Collectors.toList()));
        input.add(10L);
        input.addAll(ROUTINE_A.chars()
                              .mapToLong(i -> (long) i)
                              .boxed()
                              .collect(Collectors.toList()));
        input.add(10L);
        input.addAll(ROUTINE_B.chars()
                              .mapToLong(i -> (long) i)
                              .boxed()
                              .collect(Collectors.toList()));
        input.add(10L);
        input.addAll(ROUTINE_C.chars()
                              .mapToLong(i -> (long) i)
                              .boxed()
                              .collect(Collectors.toList()));
        input.add(10L);
        input.add((long) 'n');
        input.add(10L);
    }

    private void part1(List<String> lines) {
        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        IntCode.Instruction signal = null;
        List<Long> output = service.getIo().output;

        int x = 0;
        int y = 0;
        while (signal != IntCode.Instruction.FINISH) {
            signal = service.execute();
            Long aLong = output.get(output.size() - 1);
            char tile = (char) aLong.intValue();
            System.out.print(tile);
            if (aLong == 10) {
                y++;
                x = 0;
            } else {
                map.put(Point.of(x, y), Tile.of(tile));
                x++;
            }
        }

        //part 1 identification of intersection
        MutableSet<Point> intersections = findIntersections();

        log.info("{} intersections detected.", intersections.size());

        int sum = intersections.stream()
                               .mapToInt(Point::alignment)
                               .sum();

        log.info("Sum of alignment = {}", sum);
    }

    private MutableSet<Point> findIntersections() {
        return map.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue() == Tile.SCAFFOLD)
                  //exclude edge
                  .filter(entry -> entry.getKey().x != 0)
                  .filter(entry -> entry.getKey().x != X_MAX)
                  .filter(entry -> entry.getKey().y != 0)
                  .filter(entry -> entry.getKey().y != Y_MAX)
                  .filter(entry -> Sets.mutable.of(Direction.UP.computeNextPoint(entry.getKey()),
                                                   Direction.RIGHT.computeNextPoint(entry.getKey()),
                                                   Direction.DOWN.computeNextPoint(entry.getKey()),
                                                   Direction.LEFT.computeNextPoint(entry.getKey()))
                                               .stream()
                                               .map(point -> map.getOrDefault(point, Tile.EMPTY))
                                               .allMatch(tile -> tile == Tile.SCAFFOLD))
                  .map(Map.Entry::getKey)
                  .collect(Collectors2.toSet());
    }

    public enum Tile {
        EMPTY('.'),
        DROID_DOWN(Direction.DOWN.getCode()),
        DROID_RIGHT(Direction.RIGHT.getCode()),
        DROID_UP(Direction.UP.getCode()),
        DROID_LEFT(Direction.LEFT.getCode()),
        SCAFFOLD('#'),
        DROID_LOST('X');

        @Getter
        private char code;

        Tile(char code) {
            this.code = code;
        }

        public static Tile of(char code) {
            return Arrays.stream(values())
                         .filter(tileId -> tileId.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Tile " + code + " is not recognised."));
        }

        public boolean isDroid() {
            return this.name().startsWith("DROID");
        }
    }

    @Getter
    public enum Direction {
        DOWN('v', point -> point.toBuilder().y(point.y - 1).build()),
        RIGHT('>', point -> point.toBuilder().x(point.x + 1).build()),
        UP('^', point -> point.toBuilder().y(point.y + 1).build()),
        LEFT('<', point -> point.toBuilder().x(point.x - 1).build());

        private char code;
        private Function<Point, Point> nextPointComputer;

        Direction(char code, Function<Point, Point> nextPointComputer) {
            this.code = code;
            this.nextPointComputer = nextPointComputer;
        }

        public static Direction of(char code) {
            return Arrays.stream(values())
                         .filter(direction -> direction.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Direction " + code + " is not recognised."));
        }

        public Point computeNextPoint(Point currentPosition) {
            return this.nextPointComputer.apply(currentPosition);
        }
    }
}
