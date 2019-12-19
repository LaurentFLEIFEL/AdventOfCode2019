package com.lf.advent.service;

import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.junit.Test;

import java.util.List;

public class Vault2Test {


    @Test
    public void test1() {
        //Given
        List<String> lines = Lists.mutable.of("#########",
                                              "#b.A.@.a#",
                                              "#########");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(8);
    }

    @Test
    public void test2() {
        //Given
        List<String> lines = Lists.mutable.of("########################",
                                              "#f.D.E.e.C.b.A.@.a.B.c.#",
                                              "######################.#",
                                              "#d.....................#",
                                              "########################");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(86);
    }

    @Test
    public void test3() {
        //Given
        List<String> lines = Lists.mutable.of("########################",
                                              "#...............b.C.D.f#",
                                              "#.######################",
                                              "#.....@.a.B.c.d.A.e.F.g#",
                                              "########################");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(132);
    }

    @Test
    public void test4() {
        //Given
        List<String> lines = Lists.mutable.of("#################",
                                              "#i.G..c...e..H.p#",
                                              "########.########",
                                              "#j.A..b...f..D.o#",
                                              "########@########",
                                              "#k.E..a...g..B.n#",
                                              "########.########",
                                              "#l.F..d...h..C.m#",
                                              "#################");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(136);
    }

    @Test
    public void test5() {
        //Given
        List<String> lines = Lists.mutable.of("########################",
                                              "#@..............ac.GI.b#",
                                              "###d#e#f################",
                                              "###A#B#C################",
                                              "###g#h#i################",
                                              "########################");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(81);
    }

    @Test
    public void test2_1() {
        //Given
        List<String> lines = Lists.mutable.of("#######",
                                              "#a.#Cd#",
                                              "##@#@##",
                                              "#######",
                                              "##@#@##",
                                              "#cB#.b#",
                                              "#######");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(8);
    }

    @Test
    public void test2_2() {
        //Given
        List<String> lines = Lists.mutable.of("###############",
                                              "#d.ABC.#.....a#",
                                              "######@#@######",
                                              "###############",
                                              "######@#@######",
                                              "#b.....#.....c#",
                                              "###############");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(24);
    }

    @Test
    public void test2_3() {
        //Given
        List<String> lines = Lists.mutable.of("#############",
                                              "#DcBa.#.GhKl#",
                                              "#.###@#@#I###",
                                              "#e#d#####j#k#",
                                              "###C#@#@###J#",
                                              "#fEbA.#.FgHi#",
                                              "#############");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(32);
    }

    @Test
    public void test2_4() {
        //Given
        List<String> lines = Lists.mutable.of("#############",
                                              "#g#f.D#..h#l#",
                                              "#F###e#E###.#",
                                              "#dCba@#@BcIJ#",
                                              "#############",
                                              "#nK.L@#@G...#",
                                              "#M###N#H###.#",
                                              "#o#m..#i#jk.#",
                                              "#############");

        //When
        Vault2 service = new Vault2();
        service.consume(lines);

        //Then
        Assertions.assertThat(service.pathSize()).isEqualTo(72);
    }
}