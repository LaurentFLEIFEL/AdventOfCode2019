package com.lf.advent.service;

import com.lf.advent.util.Point;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static org.eclipse.collections.impl.collector.Collectors2.toImmutableSet;
import static org.eclipse.collections.impl.tuple.Tuples.pair;

@Service
@Slf4j
public class Vault2 implements LinesConsumer {

    private Map<Point, Tile> map = Maps.mutable.empty();
    private ImmutableSet<Tile> allKeys = Sets.immutable.empty();

    private Map<Pair<Tile, Tile>, PathInfo> distances = Maps.mutable.withInitialCapacity(30 * 30);

    private ImmutableSet<Tile> starts = Sets.immutable.empty();
    private ImmutableSet<Tile> nodes;
    private Result bestDistance;
    private Map<PathToKeysKeyMap, Result> memoizedValues = Maps.mutable.withInitialCapacity(10_000);

    @Override
    public void consume(List<String> lines) {
        initializeMap(lines);

        computeDistances();

        bestDistance = findBestDistance2(starts, Sets.immutable.empty());
        addMissingNode();
        log.info("Best distance = {}", bestDistance);
    }

    private void addMissingNode() {
        nodes.difference(bestDistance.getPath().toSet())
             .forEach(tile -> bestDistance.getPath().newWith(tile));//only the last one is missing
    }

    private Result findBestDistance2(ImmutableSet<Tile> currents, ImmutableSet<Tile> availableKeys) {
        if (availableKeys.containsAllIterable(allKeys)) {
            return Result.of(Lists.immutable.empty(), 0);
        }

        PathToKeysKeyMap key = PathToKeysKeyMap.builder()
                                               .start(currents)
                                               .availableKeys(availableKeys)
                                               .build();
        if (memoizedValues.containsKey(key)) {
            return memoizedValues.get(key);
        }


        Result result = reachableKeys2(currents, availableKeys).stream()
                                                               .map(pair -> evaluateDistance(currents, availableKeys, pair))
                                                               .min(comparingInt(Result::getLength))
                                                               .orElse(Result.of(Lists.immutable.empty(), Integer.MAX_VALUE));

        memoizedValues.put(key, result);
        return result;
    }

    private Result evaluateDistance(ImmutableSet<Tile> currents, ImmutableSet<Tile> availableKeys, Pair<Tile, Tile> pair) {
        ImmutableSet<Tile> newAvailableKeys = availableKeys.toImmutable()
                                                           .newWith(pair.getTwo());
        ImmutableSet<Tile> newCurrents = currents.toImmutable()
                                                 .reject(tile -> tile.equals(pair.getOne()))
                                                 .newWith(pair.getTwo());
        Result partialResult = findBestDistance2(newCurrents, newAvailableKeys);
        int length = distances.get(pair).getLength() + partialResult.getLength();
        ImmutableList<Tile> path = Lists.immutable.of(pair.getOne()).newWithAll(partialResult.getPath());
        return Result.of(path, length);
    }

    private ImmutableSet<Pair<Tile, Tile>> reachableKeys2(ImmutableSet<Tile> currents, ImmutableSet<Tile> availableKeys) {
        return currents.stream()
                       .map(current -> reachableKeys(current, availableKeys))
                       .flatMap(ImmutableSet::stream)
                       .collect(toImmutableSet());
    }

    private ImmutableSet<Pair<Tile, Tile>> reachableKeys(Tile current, ImmutableSet<Tile> availableKeys) {
        ImmutableSet<Tile> difference = allKeys.difference(availableKeys);
        return difference.stream()
                         .map(node -> pair(current, node))
                         .map(pair -> distances.get(pair))
                         .filter(Objects::nonNull)
                         .filter(pathInfo -> pathInfo.canUnlock(availableKeys.collect(Tile::getCode)))
                         .map(pathInfo -> map.get(pathInfo.getLast()))
                         .map(reachableKey -> pair(current, reachableKey))
                         .collect(toImmutableSet());
    }

    private void computeDistances() {
        nodes = allKeys.union(starts);

        nodes.forEach(node -> nodes.stream()
                                   .filter(o -> !node.equals(o))
                                   .map(node2 -> pair(node, node2))
                                   .forEach(this::computeDistance));
    }

    public int pathSize() {
        return bestDistance.getLength();
    }

    private void computeDistance(Pair<Tile, Tile> pair) {
        if (distances.containsKey(pair)) {
            return;
        }

        PathInfo pathInfo = path(pair.getOne().getPosition(), pair.getTwo().getPosition());
        if (pathInfo == null) {
            return;
        }
        distances.put(pair, pathInfo);
        PathInfo copy = pathInfo.copy();
        copy.setLast(pair.getOne().getPosition());
        distances.put(pair.swap(), copy);
    }

    private void initializeMap(List<String> lines) {
        int xMax = lines.get(0).length();
        int yMax = lines.size();
        for (int y = 0; y < yMax; y++) {
            String[] line = lines.get(y).split("");
            for (int x = 0; x < xMax; x++) {
                if (" ".equals(line[x])) {
                    continue;
                }
                Point position = Point.of(x, y);
                Tile tile = Tile.of(position, line[x]);
                if (tile.getType() == TileType.START) {
                    starts = starts.newWith(tile);
                }

                if (tile.isKey()) {
                    allKeys = allKeys.newWith(tile);
                }
                map.put(position, tile);
            }
        }
    }

    public PathInfo path(Point start, Point target) {
        Set<PathInfo> paths = Sets.mutable.of(PathInfo.of(start));
        Set<Point> visited = Sets.mutable.of(start);

        while (paths.stream().noneMatch(path -> path.getLast().equals(target))) {
            Set<PathInfo> aux = Sets.mutable.empty();
            for (PathInfo path : paths) {
                Point lastVisited = path.getLast();
                Stream.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH)
                      .map(direction -> direction.computeNextPoint(lastVisited))
                      .filter(point -> !visited.contains(point))
                      .filter(point -> map.containsKey(point))
                      .map(point -> map.get(point))
                      .filter(tile -> !tile.isWall())
                      .peek(tile -> visited.add(tile.getPosition()))
                      .map(tile -> {
                          PathInfo pathInfo = path.copy();
                          pathInfo.addTile(tile);
                          return pathInfo;
                      })
                      .forEach(aux::add);
            }

            if (aux.isEmpty()) {
                return null;//No path possible
            }

            paths = aux;
        }


        return paths.stream()
                    .filter(pathInfo -> pathInfo.getLast().equals(target))
                    .min(comparingInt(PathInfo::getLength))
                    .orElseThrow(() -> new IllegalStateException("Not possible."));
    }

    @Builder
    @Getter
    public static class Result {
        private ImmutableList<Tile> path;
        private int length;

        public static Result of(ImmutableList<Tile> path, int length) {
            return Result.builder()
                         .path(path)
                         .length(length)
                         .build();
        }

        @Override
        public String toString() {
            return "Result{" + length + path + '}';
        }
    }

    public enum TileType {
        WALL("#"::equals),
        DOOR(s -> StringUtils.isAlpha(s) && StringUtils.isAllUpperCase(s)),
        KEY(s -> StringUtils.isAlpha(s) && StringUtils.isAllLowerCase(s)),
        START("@"::equals),
        EMPTY("."::equals);

        private Predicate<String> isThisOne;

        TileType(Predicate<String> isThisOne) {
            this.isThisOne = isThisOne;
        }

        public static TileType of(String code) {
            return Arrays.stream(values())
                         .filter(tileType -> tileType.isThisOne.test(code))
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("TileType " + code + " is not recognised."));
        }
    }

    @Getter
    public enum Direction {
        SOUTH(2, point -> point.toBuilder().y(point.y + 1).build()),
        EAST(4, point -> point.toBuilder().x(point.x + 1).build()),
        NORTH(1, point -> point.toBuilder().y(point.y - 1).build()),
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

    @Builder
    @EqualsAndHashCode
    public static class PathToKeysKeyMap {
        private ImmutableSet<Tile> start;
        private ImmutableSet<Tile> availableKeys;
    }

    @Getter
    @EqualsAndHashCode
    public static class Tile {
        private Point position;
        private TileType type;
        private String code;

        public static Tile of(Point position, String code) {
            TileType type = TileType.of(code);
            Tile tile = new Tile();
            tile.position = position;
            tile.type = type;
            tile.code = code;
            return tile;
        }

        @Override
        public String toString() {
            return "'" + code + "(" + position.x + ", " + position.y + ")'";
        }

        public boolean isKey() {
            return type == TileType.KEY;
        }

        public boolean isDoor() {
            return type == TileType.DOOR;
        }

        public boolean isWall() {
            return type == TileType.WALL;
        }
    }

    @Getter
    @ToString
    public static class PathInfo {
        private int length;
        private Set<String> doors = Sets.mutable.empty();
        @Setter
        private Point last;

        public PathInfo(Point last) {
            this.last = last;
        }

        public static PathInfo of(Point last) {
            return new PathInfo(last);
        }

        public PathInfo copy() {
            PathInfo pathInfo = new PathInfo(this.last);
            pathInfo.doors.addAll(this.doors);
            pathInfo.length = this.length;
            return pathInfo;
        }

        public void addTile(Tile tile) {
            if (tile.isDoor()) {
                doors.add(tile.getCode().toLowerCase());
            }
            last = tile.getPosition();
            length++;
        }

        public boolean canUnlock(ImmutableSet<String> availableKeys) {
            return availableKeys.containsAll(doors);
        }
    }
}
