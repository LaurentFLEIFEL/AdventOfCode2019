package com.lf.advent.service;

import com.lf.advent.util.Point;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MonitoringStation implements LinesConsumer {

    Map<Point, Long> viewByAsteroid = Maps.mutable.empty();
    Set<Point> asteroids = Sets.mutable.empty();
    @Getter
    private Point maxViewer;

    @Override
    public void consume(List<String> lines) {
        int height = lines.size();
        int width = lines.get(0).length();
        for (int i = 0; i < height; i++) {
            String line = lines.get(i);
            for (int j = 0; j < width; j++) {
                if (line.charAt(j) == '#') {
                    asteroids.add(Point.of(j, i));
                }
            }
        }

        asteroids.forEach(point -> viewByAsteroid.put(point, point.findVisible(asteroids)));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point point = Point.of(j, i);
                if (!asteroids.contains(point)) {
                    System.out.print(" .  ");
                    continue;
                }
                System.out.printf("%4d", viewByAsteroid.get(point));
            }
            System.out.println();
        }

        maxViewer = viewByAsteroid.entrySet()
                                  .stream()
                                  .max(Comparator.comparingLong(Map.Entry::getValue))
                                  .map(Map.Entry::getKey)
                                  .orElse(Point.ZERO);

        log.info("maxViewer = {}, nbr of view = {}", maxViewer, viewByAsteroid.get(maxViewer));

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Point point = Point.of(j, i);
                if (!asteroids.contains(point)) {
                    System.out.print(" .   ");
                    continue;
                }

                if (point.equals(maxViewer)) {
                    System.out.print("  X  ");
                }
                System.out.printf("%4.3f ", point.argument(maxViewer));
            }
            System.out.println();
        }

        NavigableMap<Double, NavigableSet<Point>> asteroidsByOrigin = asteroids.stream()
                                                                               .filter(point -> !point.equals(maxViewer))
                                                                               .collect(Collectors.groupingBy(point -> point.argument(maxViewer),
                                                                                                              TreeMap::new,
                                                                                                              Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingDouble(point -> point.distance2(maxViewer))))));

//        asteroidsByOrigin.forEach((argument, points) -> System.out.println("arguments = " + argument + " points = " + points));

        Set<Point> destroyedPoints = Sets.mutable.empty();
        double previousArgument = -1d;
        while (destroyedPoints.size() < asteroids.size() - 1) {
            Map.Entry<Double, NavigableSet<Point>> entry = asteroidsByOrigin.higherEntry(previousArgument);
            if (entry == null) {
                previousArgument = -1d;
                continue;
            }
            previousArgument = entry.getKey();
            Optional<Point> first = entry.getValue()
                                         .stream()
                                         .filter(point -> !destroyedPoints.contains(point))
                                         .findFirst();
            if (!first.isPresent()) {
                continue;
            }

            log.info("{} th to be vaporized at {}", destroyedPoints.size() + 1, first.get());
            destroyedPoints.add(first.get());
        }
    }
}
