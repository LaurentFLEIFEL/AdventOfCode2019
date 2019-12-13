package com.lf.advent.service;

import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.FixedSizeList;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapOrbiterTest {

    private MapOrbiter mapOrbiter = new MapOrbiter();

    @Test
    public void test1() {
        //Given
        FixedSizeList<String> lines = Lists.fixedSize.of("COM)B",
                                                         "B)C",
                                                         "C)D",
                                                         "D)E",
                                                         "E)F",
                                                         "B)G",
                                                         "G)H",
                                                         "D)I",
                                                         "E)J",
                                                         "J)K",
                                                         "K)L");

        //When
        mapOrbiter.consume(lines);

        //Then
        Assertions.assertThat(mapOrbiter.getCount()).isEqualTo(42);
    }

    @Test
    public void test2() {
        //Given
        FixedSizeList<String> lines = Lists.fixedSize.of("COM)B",
                                                         "B)C",
                                                         "C)D",
                                                         "D)E",
                                                         "E)F",
                                                         "B)G",
                                                         "G)H",
                                                         "D)I",
                                                         "E)J",
                                                         "J)K",
                                                         "K)L",
                                                         "K)YOU",
                                                         "I)SAN");

        //When
        mapOrbiter.consume(lines);

        //Then
        Assertions.assertThat(mapOrbiter.getMinTransfer()).isEqualTo(4);
    }
}