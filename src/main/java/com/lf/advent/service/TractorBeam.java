package com.lf.advent.service;

import com.lf.advent.util.Point;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TractorBeam implements LinesConsumer {

    public static final int X_MAX = 1050;
    public static final int Y_MAX = 1200;
    public static final int X_MIN = 650;
    public static final int Y_MIN = 1000;
    public static final int SHIP_WIDTH = 100;
    public static final int SHIP_HEIGHT = 100;
    private Map<Point, DroneEffect> maps = Maps.mutable.withInitialCapacity((X_MAX - X_MIN) * (Y_MAX - Y_MIN));

    @Override
    public void consume(List<String> lines) {
        initialize(lines);

//        display();

        part1();

        part2();
    }

    private void part2() {
        for (int y = Y_MIN; y < Y_MAX; y++) {
            for (int x = X_MIN; x < X_MAX; x++) {
                Point of = Point.of(x, y);
                if (isSantaShipFit(of)) {
                    log.info("Santa ship fit in {}", of);
                    log.info("Result = {}", x * 10_000 + y);
                    return;
                }
            }
        }
    }

    private boolean isSantaShipFit(Point of) {
        for (int x = of.x; x < of.x + SHIP_WIDTH; x++) {
            for (int y = of.y; y < of.y + SHIP_HEIGHT; y++) {
                if (maps.getOrDefault(Point.of(x, y), DroneEffect.STATIONARY) == DroneEffect.STATIONARY) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initialize(List<String> lines) {
        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        for (int y = Y_MIN; y < Y_MAX; y++) {
            for (int x = X_MIN; x < X_MAX; x++) {
                DroneEffect of = DroneEffect.of(isPulled(memory, x, y));
                maps.put(Point.of(x, y), of);
            }
        }
    }

    private void part1() {
        long count = maps.values()
                         .stream()
                         .filter(droneEffect -> droneEffect == DroneEffect.PULLED)
                         .count();


        log.info("Count = {}", count);
    }

    public void display() {
        for (int y = Y_MIN; y < Y_MAX; y++) {
            for (int x = X_MIN; x < X_MAX; x++) {
                Point point = Point.of(x, y);
                DroneEffect droneEffect = maps.getOrDefault(point, DroneEffect.STATIONARY);
                System.out.print(droneEffect.getDisplay());
            }
            System.out.println();
        }
    }

    public int isPulled(long[] memory, int x, int y) {
        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        service.getIo().input.add((long) x);
        service.getIo().input.add((long) y);

        service.execute();
        return service.getIo().output.get(service.getIo().output.size() - 1).intValue();
    }

    public enum DroneEffect {
        STATIONARY(0, "."),
        PULLED(1, "#");

        private int code;
        @Getter
        private String display;

        DroneEffect(int code, String display) {
            this.code = code;
            this.display = display;
        }

        public static DroneEffect of(int code) {
            return Arrays.stream(values())
                         .filter(droneEffect -> droneEffect.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("DroneEffect " + code + " is not recognised."));
        }
    }
}
