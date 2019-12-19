package com.lf.advent.service;

import com.lf.advent.util.Point;
import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.junit.Test;

public class MonitoringStationTest {
    MonitoringStation service = new MonitoringStation();

    @Test
    public void test1() {
        //Given
        service = new MonitoringStation();
        MutableList<String> lines = Lists.mutable.of(".#..#",
                                                     ".....",
                                                     "#####",
                                                     "....#",
                                                     "...##");

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMaxViewer()).isEqualTo(Point.of(3, 4));
    }

    @Test
    public void test2() {
        //Given
        service = new MonitoringStation();
        MutableList<String> lines = Lists.mutable.of("......#.#.",
                                                     "#..#.#....",
                                                     "..#######.",
                                                     ".#.#.###..",
                                                     ".#..#.....",
                                                     "..#....#.#",
                                                     "#..#....#.",
                                                     ".##.#..###",
                                                     "##...#..#.",
                                                     ".#....####");

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMaxViewer()).isEqualTo(Point.of(5, 8));
    }

    @Test
    public void test3() {
        //Given
        service = new MonitoringStation();
        MutableList<String> lines = Lists.mutable.of("#.#...#.#.",
                                                     ".###....#.",
                                                     ".#....#...",
                                                     "##.#.#.#.#",
                                                     "....#.#.#.",
                                                     ".##..###.#",
                                                     "..#...##..",
                                                     "..##....##",
                                                     "......#...",
                                                     ".####.###.");

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMaxViewer()).isEqualTo(Point.of(1, 2));
    }

    @Test
    public void test4() {
        //Given
        service = new MonitoringStation();
        MutableList<String> lines = Lists.mutable.of(".#..#..###",
                                                     "####.###.#",
                                                     "....###.#.",
                                                     "..###.##.#",
                                                     "##.##.#.#.",
                                                     "....###..#",
                                                     "..#.#..#.#",
                                                     "#..#.#.###",
                                                     ".##...##.#",
                                                     ".....#.#..");

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMaxViewer()).isEqualTo(Point.of(6, 3));
    }

    @Test
    public void test5() {
        //Given
        service = new MonitoringStation();
        MutableList<String> lines = Lists.mutable.of(".#..##.###...#######",
                                                     "##.############..##.",
                                                     ".#.######.########.#",
                                                     ".###.#######.####.#.",
                                                     "#####.##.#.##.###.##",
                                                     "..#####..#.#########",
                                                     "####################",
                                                     "#.####....###.#.#.##",
                                                     "##.#################",
                                                     "#####.##.###..####..",
                                                     "..######..##.#######",
                                                     "####.##.####...##..#",
                                                     ".#####..#.######.###",
                                                     "##...#.##########...",
                                                     "#.##########.#######",
                                                     ".####.#.###.###.#.##",
                                                     "....##.##.###..#####",
                                                     ".#.#.###########.###",
                                                     "#.#.#.#####.####.###",
                                                     "###.##.####.##.#..##");

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMaxViewer()).isEqualTo(Point.of(11, 13));
    }

    @Test
    public void test() {
        //Given
        service = new MonitoringStation();
        MutableList<String> lines = Lists.mutable.of("....#.....#.#...##..........#.......#......",
                                                     ".....#...####..##...#......#.........#.....",
                                                     ".#.#...#..........#.....#.##.......#...#..#",
                                                     ".#..#...........#..#..#.#.......####.....#.",
                                                     "##..#.................#...#..........##.##.",
                                                     "#..##.#...#.....##.#..#...#..#..#....#....#",
                                                     "##...#.............#.#..........#...#.....#",
                                                     "#.#..##.#.#..#.#...#.....#.#.............#.",
                                                     "...#..##....#........#.....................",
                                                     "##....###..#.#.......#...#..........#..#..#",
                                                     "....#.#....##...###......#......#...#......",
                                                     ".........#.#.....#..#........#..#..##..#...",
                                                     "....##...#..##...#.....##.#..#....#........",
                                                     "............#....######......##......#...#.",
                                                     "#...........##...#.#......#....#....#......",
                                                     "......#.....#.#....#...##.###.....#...#.#..",
                                                     "..#.....##..........#..........#...........",
                                                     "..#.#..#......#......#.....#...##.......##.",
                                                     ".#..#....##......#.............#...........",
                                                     "..##.#.....#.........#....###.........#..#.",
                                                     "...#....#...#.#.......#...#.#.....#........",
                                                     "...####........#...#....#....#........##..#",
                                                     ".#...........#.................#...#...#..#",
                                                     "#................#......#..#...........#..#",
                                                     "..#.#.......#...........#.#......#.........",
                                                     "....#............#.............#.####.#.#..",
                                                     ".....##....#..#...........###........#...#.",
                                                     ".#.....#...#.#...#..#..........#..#.#......",
                                                     ".#.##...#........#..#...##...#...#...#.#.#.",
                                                     "#.......#...#...###..#....#..#...#.........",
                                                     ".....#...##...#.###.#...##..........##.###.",
                                                     "..#.....#.##..#.....#..#.....#....#....#..#",
                                                     ".....#.....#..............####.#.........#.",
                                                     "..#..#.#..#.....#..........#..#....#....#..",
                                                     "#.....#.#......##.....#...#...#.......#.#..",
                                                     "..##.##...........#..........#.............",
                                                     "...#..##....#...##..##......#........#....#",
                                                     ".....#..........##.#.##..#....##..#........",
                                                     ".#...#...#......#..#.##.....#...#.....##...",
                                                     "...##.#....#...........####.#....#.#....#..",
                                                     "...#....#.#..#.........#.......#..#...##...",
                                                     "...##..............#......#................",
                                                     "........................#....##..#........#");

        //When
        service.consume(lines);

        //Then
//        Assertions.assertThat(service.getMaxViewer()).isEqualTo(Point.of(1, 2));
    }

    @Test
    public void t1() {
        //Given
        service = new MonitoringStation();
        Point OI = Point.of(0, 1);
        Point O2 = Point.of(0, 2);

        //When

        //Then
        Assertions.assertThat(OI.canSee(O2, Sets.mutable.of(OI, O2))).isTrue();
    }

    @Test
    public void t2() {
        //Given
        service = new MonitoringStation();
        Point OI = Point.of(0, 1);
        Point O2 = Point.of(0, 2);
        Point O3 = Point.of(0, 3);

        //When

        //Then
        Assertions.assertThat(OI.canSee(O3, Sets.mutable.of(OI, O2, O3))).isFalse();
    }
}