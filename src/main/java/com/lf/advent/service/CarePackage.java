package com.lf.advent.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Maps;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CarePackage implements LinesConsumer {
    private Map<Point, TileId> map = Maps.mutable.empty();

    @Override
    public void consume(List<String> lines) {

        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        memory[0] = 2L;//play for free

        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        IntCode.Instruction signal = null;
        Point paddle = null;
        Point ball = null;
        Point previousBall = null;
        MyInput input = new MyInput();
        service.getIo().input = input;
        List<Long> output = service.getIo().output;
        MutableBag<TileId> tileIds;
        Long score = -1L;
        while (signal != IntCode.Instruction.FINISH) {
            signal = service.execute();
            signal = service.execute();
            signal = service.execute();
            int length = output.size();
            int x = output.get(length - 3).intValue();
            int y = output.get(length - 2).intValue();

            if (x == -1 && y == 0) {
                score = output.get(length - 1);
                log.info("score = {}", score);
                continue;
            }
            TileId tileId = TileId.of(output.get(length - 1).intValue());
            map.put(Point.of(x, y), tileId);
            if (tileId == TileId.PADDLE) {
                paddle = Point.of(x, y);
            }
            if (tileId == TileId.BALL) {
                if (ball != null) {
                    previousBall = ball.toBuilder().build();
                }
                ball = Point.of(x, y);
            }

            input.paddle = paddle;
            input.ball = ball;
            input.previousBall = previousBall;
            tileIds = Bags.mutable.ofAll(map.values());
            if (tileIds.occurrencesOf(TileId.PADDLE) == 1 && tileIds.occurrencesOf(TileId.BALL) == 1 && score != -1) {
                display();
            }
        }


        tileIds = Bags.mutable.ofAll(map.values());
        log.info("Block tiles = {}", tileIds.occurrencesOf(TileId.BLOCK));
        log.info("score = {}", score);
    }

    public static class MyInput extends ArrayDeque<Long> {

        Point paddle = null;
        Point ball = null;
        Point previousBall = null;

        @Override
        public Long pollFirst() {
            if (paddle.x < ball.x) {
                return 1L;
            } else if (paddle.x > ball.x) {
                return -1L;
            } else {
                if (Objects.isNull(previousBall)) {
                    return 0L;
                } else {
                    if (previousBall.y < ball.y) {
                        return 0L;
                    }
                    if (paddle.x < previousBall.x) {
                        return -1L;
                    } else if (paddle.x > previousBall.x) {
                        return 1L;
                    } else {
                        return 0L;
                    }
                }
            }
        }
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

        int width = xs.getMax() - xs.getMin() + 1;
        int height = ys.getMax() - ys.getMin() + 1;
//        log.info("Width = {}, Height = {}", width, height);

        if (width != 44 || height != 20) {
            return;
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int y = ys.getMax(); y > ys.getMin() - 1; y--) {
            for (int x = xs.getMin(); x < xs.getMax(); x++) {
                Point point = Point.of(x, y);
                TileId tile = map.getOrDefault(point, TileId.EMPTY);
                System.out.print(tile.getDisplay());
            }
            System.out.println();
        }
    }

    enum TileId {
        EMPTY(0, " "),
        WALL(1, "+"),
        BLOCK(2, "*"),
        PADDLE(3, "_"),
        BALL(4, "O");

        private int code;
        @Getter
        private String display;

        TileId(int code, String display) {
            this.code = code;
            this.display = display;
        }

        public static TileId of(int code) {
            return Arrays.stream(values())
                         .filter(tileId -> tileId.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("TileId " + code + " is not recognised."));
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
