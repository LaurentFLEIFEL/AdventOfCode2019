package com.lf.advent.service;

import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.junit.Test;

import java.util.List;

public class WireClosestTest {

    private WireClosest service = new WireClosest();

    @Test
    public void test1() {
        //Given
        List<String> lines = Lists.fixedSize.of("R8,U5,L5,D3", "U7,R6,D4,L4");
        int expected = 6;

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getClosestIntersection()).isEqualTo(expected);
    }

    @Test
    public void test2() {
        //Given
        List<String> lines = Lists.fixedSize.of("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83");
        int expected = 159;

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getClosestIntersection()).isEqualTo(expected);
    }

    @Test
    public void test3() {
        //Given
        List<String> lines = Lists.fixedSize.of("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7");
        int expected = 135;

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getClosestIntersection()).isEqualTo(expected);
    }

    @Test
    public void test4() {
        //Given
        List<String> lines = Lists.fixedSize.of("R8,U5,L5,D3", "U7,R6,D4,L4");
        int expected = 30;

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMinStep()).isEqualTo(expected);
    }

    @Test
    public void test5() {
        //Given
        List<String> lines = Lists.fixedSize.of("R75,D30,R83,U83,L12,D49,R71,U7,L72", "U62,R66,U55,R34,D71,R55,D58,R83");
        int expected = 610;

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMinStep()).isEqualTo(expected);
    }

    @Test
    public void test6() {
        //Given
        List<String> lines = Lists.fixedSize.of("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51", "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7");
        int expected = 410;

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getMinStep()).isEqualTo(expected);
    }
}