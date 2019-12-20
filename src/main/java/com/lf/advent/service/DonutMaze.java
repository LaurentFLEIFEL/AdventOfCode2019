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
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.tuple.Tuples;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static org.eclipse.collections.impl.tuple.Tuples.pair;

@Service
@Slf4j
public class DonutMaze implements LinesConsumer {

    private Map<Point, Tile> map = Maps.mutable.empty();
    private Map<Point, Portal> por = Maps.mutable.empty();
    private Map<Point, MutableList<Portal>> nPor = Maps.mutable.empty();
    private ImmutableSet<Portal> portals = Sets.immutable.empty();
    private Map<Pair<Point, Point>, PathInfo> distances = Maps.mutable.withInitialCapacity(30 * 30);
    private PortalPathInfo bestPath;
    private MutableSet<Portal> newPortals;
    private Map<Portal, Integer> dDji = Maps.mutable.empty();
    private Map<Portal, Portal> predecessors = Maps.mutable.empty();
    private Map<Portal, Integer> bestByPortal = Maps.mutable.empty();
    private Map<MemoizedKey, PortalPathInfo> memoized = Maps.mutable.withInitialCapacity(10_000);
    private Map<Pair<Portal, ImmutableSet<Portal>>, ImmutableSet<Portal>> memoizeReachables = Maps.mutable.withInitialCapacity(10_000);

    @Override
    public void consume(List<String> lines) {
        initializeMap(lines);

        findPortals();

        computeDistances();

        computeInOut();

        buildNewPortals();

        Portal start = newPortals.detect(portal -> portal.getCode().equals("AA"));
        Portal target = newPortals.detect(portal -> portal.getCode().equals("ZZ"));

        djikstra(start);
        List<Portal> path = applyDjikstra(start, target);

        log.info("Djikstra path = {}", path);
        int length = path.stream()
                         .mapToInt(portal -> dDji.get(portal))
                         .sum();

        log.info("Length = {}", length);

//        bestPath = pathPortal2(start, Sets.immutable.empty(), target);
//        log.info("Best path = {}", bestPath.getPath());
//        log.info("Best path length = {}", bestPath.getLength());
    }

    private void buildNewPortals() {
        newPortals = Sets.mutable.empty();
        ImmutableSet<Portal> level0 = portals.select(portal -> portal.isEntrance() || portal.isInner());
        ImmutableSet<Portal> level1 = portals.select(portal -> portal.isOuter() && !portal.isEntrance())
                                             .collect(portal -> portal.copy(1));
        ImmutableSet<Portal> level2 = portals.select(Portal::isInner)
                                             .collect(portal -> portal.copy(2));

        level0.forEach(portal -> nPor.computeIfAbsent(portal.getPosition(), p -> Lists.mutable.empty()).with(portal));
        level1.forEach(portal -> nPor.computeIfAbsent(portal.getPosition(), p -> Lists.mutable.empty()).with(portal));
        level2.forEach(portal -> nPor.computeIfAbsent(portal.getPosition(), p -> Lists.mutable.empty()).with(portal));

        newPortals.addAllIterable(level0);
        newPortals.addAllIterable(level1);
        newPortals.addAllIterable(level2);

        for (int level = 3; level < 100; level++) {
            int finalLevel = level;
            if (level % 2 == 0) {
                ImmutableSet<Portal> levelEven = level2.collect(portal -> portal.copy(finalLevel));
                newPortals.addAllIterable(levelEven);
                levelEven.forEach(portal -> nPor.computeIfAbsent(portal.getPosition(), p -> Lists.mutable.empty()).with(portal));
            } else {
                ImmutableSet<Portal> levelOdd = level1.collect(portal -> portal.copy(finalLevel));
                newPortals.addAllIterable(levelOdd);
                levelOdd.forEach(portal -> nPor.computeIfAbsent(portal.getPosition(), p -> Lists.mutable.empty()).with(portal));
            }
        }

    }

    private void computeInOut() {
        IntSummaryStatistics xs = map.values()
                                     .stream()
                                     .filter(Tile::isWall)
                                     .map(Tile::getPosition)
                                     .mapToInt(Point::getX)
                                     .summaryStatistics();
        IntSummaryStatistics ys = map.values()
                                     .stream()
                                     .filter(Tile::isWall)
                                     .map(Tile::getPosition)
                                     .mapToInt(Point::getY)
                                     .summaryStatistics();

        portals.forEach(portal -> portal.setOuter(!(portal.getPosition().x > xs.getMin()
                                                    && portal.getPosition().x < xs.getMax()
                                                    && portal.getPosition().y > ys.getMin()
                                                    && portal.getPosition().y < ys.getMax())));
    }

    public int pathSize() {
        return bestPath.getLength();
    }

    private void initDji(Portal start) {
        newPortals.forEach(portal -> dDji.put(portal, Integer.MAX_VALUE));
        dDji.put(start, 0);
    }

    private Portal findMin(ImmutableSet<Portal> visited) {
        return newPortals.difference(visited)
                         .min(comparingInt(portal -> dDji.get(portal)));
    }

    private void majdDji(Portal p1, Portal p2) {
        if (distances.get(Tuples.pair(p1.getPosition(), p2.getPosition())) == null) {
            return;
        }
        if (dDji.get(p2) > dDji.get(p1) + distances.get(Tuples.pair(p1.getPosition(), p2.getPosition())).getLength()) {
            dDji.put(p2, dDji.get(p1) + distances.get(Tuples.pair(p1.getPosition(), p2.getPosition())).getLength());
            predecessors.put(p2, p1);
        }
    }

    private void djikstra(Portal start) {
        initDji(start);
        ImmutableSet<Portal> visited = Sets.immutable.empty();
        while (visited.size() < newPortals.size()) {
            Portal min = findMin(visited);
            visited = visited.newWith(min);
            ImmutableSet<Portal> reachables = reachablePortals2(min, visited);
            for (Portal portal : reachables) {
                majdDji(min, portal);
            }
        }
    }

    private List<Portal> applyDjikstra(Portal start, Portal target) {
        List<Portal> result = Lists.mutable.empty();
        Portal aux = target;
        while (aux != start) {
            result.add(aux);
            aux = predecessors.get(aux);
        }
        result.add(start);

        return result;
    }

    public PortalPathInfo pathPortal2(Portal start, ImmutableSet<Portal> visited, Portal target) {
//        log.info("Calling path portal with {}, {}, {}", start, target, visited);
        if (start.equals(target)) {
            return PortalPathInfo.of(start);
        }

        MemoizedKey memoizedKey = MemoizedKey.of(start, visited, target);
        if (memoized.containsKey(memoizedKey)) {
            return memoized.get(memoizedKey);
        }

        ImmutableSet<Portal> reachablePortals = reachablePortals2(start, visited);

        PortalPathInfo result = null;
        for (Portal reachablePortal : reachablePortals) {
            PortalPathInfo portalPathInfo1 = retrievePortalPathInfo(start, visited, target, reachablePortal);
            if (portalPathInfo1 == null) continue;

            if (Objects.isNull(result) || result.getLength() > portalPathInfo1.getLength()) {
                result = portalPathInfo1;
            }
        }

        memoized.put(memoizedKey, result);
//        log.info("Result for {}, {}, {} \nis {}", start, target, visited, result);
        return result;
    }

    private PortalPathInfo retrievePortalPathInfo(Portal start, ImmutableSet<Portal> visited, Portal target, Portal reachablePortal) {
        PortalPathInfo portalPathInfo = pathPortal2(reachablePortal, visited.newWith(start), target);
        if (Objects.isNull(portalPathInfo)) {
            return null;
        }

        if (bestByPortal.containsKey(reachablePortal) && bestByPortal.get(reachablePortal) < portalPathInfo.getLength()) {
            return null;
        }
        bestByPortal.put(reachablePortal, portalPathInfo.getLength());

        PortalPathInfo portalPathInfo1 = portalPathInfo.copy();
        portalPathInfo1.setPath(Lists.mutable.of(start).withAll(portalPathInfo.getPath()));
        portalPathInfo1.setLength(distances.get(pair(start, reachablePortal)).getLength() + portalPathInfo1.getLength());
        return portalPathInfo1;
    }

    private ImmutableSet<Portal> reachablePortals2(Portal start, ImmutableSet<Portal> visited) {
        Pair<Portal, ImmutableSet<Portal>> memoizedKey = pair(start, visited);
        if (memoizeReachables.containsKey(memoizedKey)) {
            return memoizeReachables.get(memoizedKey);
        }
        ImmutableSet<Portal> result = distances.keySet()
                                               .stream()
                                               .filter(pair -> pair.getOne().equals(start.getPosition()))
                                               .map(Pair::getTwo)
                                               .map(point -> nPor.get(point))
                                               .map(list -> list.stream()
                                                                .filter(portal -> Math.abs(portal.getLevel() - start.getLevel()) <= 1)
                                                                .filter(portal -> reduce(start, portal, visited)))
                                               .flatMap(Function.identity())
                                               .filter(portal -> !visited.contains(portal))
                                               .collect(Collectors2.toImmutableSet());
        memoizeReachables.put(memoizedKey, result);
        return result;
    }

    private boolean reduce(Portal start, Portal portal, ImmutableSet<Portal> visited) {
        if (start.isEntrance() && portal.isEntrance()) {
            return true;
        }

        if (start.isEntrance() && portal.isOuter()) {
            return false;
        }

        if (portal.isOuter() && portal.getLevel() == 0) {
            return false;
        }

        if (start.isOuter() && portal.isEntrance()) {
            return false;
        }

        if (start.isEntrance() && portal.isInner()) {
            return portal.getLevel() == 0;
        }
        boolean isOutOfPortal = visited.stream()
                                       .filter(visitedPortal -> !visitedPortal.equals(start))
                                       .anyMatch(visitedPortal -> visitedPortal.getCode().equals(start.getCode())
                                                                  && Math.abs(visitedPortal.getLevel() - start.getLevel()) <= 1);

        if (isOutOfPortal) {
            if (portal.getCode().equals(start.getCode())) {
                return false;
            }
            if (start.isOuter()) {
                if (portal.isOuter()) {
                    return start.getLevel() == portal.getLevel();
                }
                return start.getLevel() + 1 == portal.getLevel();
            }
            if (portal.isInner()) {
                return start.getLevel() == portal.getLevel();
            }
            return start.getLevel() - 1 == portal.getLevel();
        }

        if (start.isOuter() && portal.isInner() && start.getCode().equals(portal.getCode())) {
            return start.getLevel() == portal.getLevel() + 1;
        }
        if (start.isInner() && portal.isOuter() && start.getCode().equals(portal.getCode())) {
            return start.getLevel() == portal.getLevel() - 1;
        }

        return false;
    }

    private void computeDistances() {
        portals.forEach(node -> portals.stream()
                                       .filter(o -> !node.equals(o))
                                       .map(node2 -> pair(node.getPosition(), node2.getPosition()))
                                       .forEach(this::computeDistance));
    }

    private void computeDistance(Pair<Point, Point> pair) {
        if (distances.containsKey(pair)) {
            return;
        }

        PathInfo pathInfo;
        if (por.get(pair.getOne()).getCode().equals(por.get(pair.getTwo()).getCode())) {
            pathInfo = PathInfo.of(pair.getTwo());
            pathInfo.setLength(1);
        } else {
            pathInfo = path(pair.getOne(), pair.getTwo());
        }

        if (pathInfo == null) {
            return;
        }
        distances.put(pair, pathInfo);
        PathInfo copy = pathInfo.copy();
        copy.setLast(pair.getOne());
        distances.put(pair.swap(), copy);
    }

    public PathInfo path(Point start, Point target) {
        Set<PathInfo> paths = Sets.mutable.of(PathInfo.of(start));
        Set<Point> visited = Sets.mutable.of(start);

        while (paths.stream().noneMatch(path -> path.getLast().equals(target))) {
            Set<PathInfo> aux = Sets.mutable.empty();
            for (PathInfo path : paths) {
                Point lastVisited = path.getLast();
                Stream.of(Vault2.Direction.EAST, Vault2.Direction.WEST, Vault2.Direction.NORTH, Vault2.Direction.SOUTH)
                      .map(direction -> direction.computeNextPoint(lastVisited))
                      .filter(point -> !visited.contains(point))
                      .filter(point -> map.containsKey(point))
                      .map(point -> map.get(point))
                      .filter(Tile::isOpen)
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

    private void findPortals() {
        portals = map.values()
                     .stream()
                     .filter(Tile::isUl)
                     .map(tile -> Stream.of(Direction.EAST, Direction.SOUTH)
                                        .map(direction -> direction.computeNextPoint(tile.getPosition()))
                                        .filter(point -> map.containsKey(point))
                                        .map(point -> map.get(point))
                                        .filter(Tile::isUl)
                                        .map(tile2 -> Tuples.pair(tile, tile2))
                                        .findAny()
                                        .orElse(null))
                     .filter(Objects::nonNull)
                     .map(this::convertToPortal)
                     .collect(Collectors2.toImmutableSet());

        portals.forEach(portal -> por.put(portal.getPosition(), portal));
    }

    private Portal convertToPortal(Pair<Tile, Tile> pair) {
        Point position = Stream.of(Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH)
                               .map(direction -> Stream.of(direction.computeNextPoint(pair.getOne().getPosition()),
                                                           direction.computeNextPoint(pair.getTwo().getPosition())))
                               .flatMap(Function.identity())
                               .distinct()
                               .filter(point -> map.containsKey(point))
                               .map(point -> map.get(point))
                               .filter(Tile::isOpen)//only one left
                               .map(Tile::getPosition)
                               .findAny()
                               .orElseThrow(() -> new IllegalStateException("Should not happen pair = " + pair));

        return Portal.of(position, pair.getOne().getCode() + pair.getTwo().getCode());
    }

    private void initializeMap(List<String> lines) {
        int yMax = lines.size();
        for (int y = 0; y < yMax; y++) {
            String[] line = lines.get(y).split("");
            int xMax = line.length;
            for (int x = 0; x < xMax; x++) {
                if (" ".equals(line[x])) {
                    continue;
                }
                Point position = Point.of(x, y);
                Tile tile = Tile.of(position, line[x]);

                map.put(position, tile);
            }
        }
    }

    public enum TileType {
        EMPTY(" "::equals),
        OPEN("."::equals),
        WALL("#"::equals),
        UL(StringUtils::isAllUpperCase);

        private Predicate<String> isThisOne;

        TileType(Predicate<String> isThisOne) {
            this.isThisOne = isThisOne;
        }

        public static TileType of(String code) {
            return Arrays.stream(values())
                         .filter(type -> type.isThisOne.test(code))
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("TileType " + code + " is not recognised"));
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

    //    @EqualsAndHashCode
    @Getter
    public static class Portal {
        private String code;
        private Point position;
        private int level;
        @Setter
        private boolean outer;

        public static Portal of(Point position, String code) {
            Portal portal = new Portal();
            portal.position = position;
            portal.code = code;
            return portal;
        }

        public Portal copy(int level) {
            Portal copy = Portal.of(this.position, this.code);
            copy.outer = this.outer;
            copy.level = level;
            return copy;
        }

        @Override
        public String toString() {
            return "'" + code + "-" + level + "(" + position.x + ", " + position.y + ")'";
        }

        public boolean isInner() {
            return !outer;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Portal portal = (Portal) o;
            return level == portal.level &&
                   Objects.equals(code, portal.code) &&
                   Objects.equals(position, portal.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, position, level);
        }

        public boolean isEntrance() {
            return "AA".equals(code) || "ZZ".equals(code);
        }
    }

    @Getter
    @EqualsAndHashCode
    public static class Tile {
        private Point position;
        private String code;
        private TileType type;

        public static Tile of(Point position, String code) {
            TileType type = TileType.of(code);
            Tile tile = new Tile();
            tile.position = position;
            tile.type = type;
            tile.code = code;
            return tile;
        }

        public boolean isUl() {
            return type == TileType.UL;
        }

        public boolean isWall() {
            return type == TileType.WALL;
        }

        public boolean isOpen() {
            return type == TileType.OPEN;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class PathInfo {
        private int length;
        private Point last;

        public PathInfo(Point last) {
            this.last = last;
        }

        public static PathInfo of(Point last) {
            return new PathInfo(last);
        }

        public PathInfo copy() {
            PathInfo pathInfo = new PathInfo(this.last);
            pathInfo.length = this.length;
            return pathInfo;
        }

        public void addTile(Tile tile) {
            last = tile.getPosition();
            length++;
        }
    }

    @Getter
    @Setter
    public static class PortalPathInfo {
        private int length;
        private Portal last;
        private MutableList<Portal> path = Lists.mutable.empty();

        public PortalPathInfo(Portal last) {
            this.last = last;
            path.add(last);
        }

        public static PortalPathInfo of(Portal last) {
            return new PortalPathInfo(last);
        }

        public PortalPathInfo copy() {
            PortalPathInfo portalPathInfo = new PortalPathInfo(this.last);
            portalPathInfo.length = this.length;
            portalPathInfo.path.clear();
            portalPathInfo.path.addAll(this.path);
            return portalPathInfo;
        }

        public void addPortal(Portal portal, int distance) {
            last = portal;
            path.add(portal);
            length += distance;
        }

        public void addTwinPortal(Portal portal, Portal portalTwin, int distanceToFirst) {
            last = portalTwin;
            path.add(portal);
            path.add(portalTwin);
            length += distanceToFirst;
            length++;
        }

        public boolean walkThroughPortals() {
            if (path.size() < 2) {
                return true;
            }

            for (int i = 2; i < path.size(); i += 2) {
                Portal previous = path.get(i - 1);
                Portal current = path.get(i);
                if (!previous.getCode().equals(current.getCode())) {
                    return false;
                }
            }

            return true;
        }

        public boolean hasCycle() {
            if (path.size() < 2) {
                return false;
            }

            Set<Pair<Portal, Portal>> linked = Sets.mutable.ofInitialCapacity(path.size());
            for (int i = 1; i < path.size(); i += 2) {
                Pair<Portal, Portal> pair = pair(path.get(i - 1), path.get(i));
                if (linked.contains(pair)) {
                    return true;
                }
                linked.add(pair);
            }

            return false;
        }

        @Override
        public String toString() {
            return "PortalPathInfo(" +
                   "length=" + length +
                   ", path=" + path +
                   ')';
        }
    }

    @EqualsAndHashCode
    @Builder
    public static class MemoizedKey {
        private Portal start;
        private ImmutableSet<Portal> visited;
        private Portal target;

        public static MemoizedKey of(Portal start, ImmutableSet<Portal> visited, Portal target) {
            return MemoizedKey.builder()
                              .start(start)
                              .visited(visited)
                              .target(target)
                              .build();
        }
    }

}
