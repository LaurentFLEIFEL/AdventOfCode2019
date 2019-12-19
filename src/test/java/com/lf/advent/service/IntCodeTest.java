package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class IntCodeTest {

    private IntCode service = new IntCode();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test1() {
        //Given
        long[] input = {1, 9, 10, 3, 2, 3, 11, 0, 99, 30, 40, 50};
        long[] expected = {3500, 9, 10, 70, 2, 3, 11, 0, 99, 30, 40, 50};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test2() {
        //Given
        long[] input = {1, 0, 0, 0, 99};
        long[] expected = {2, 0, 0, 0, 99};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test3() {
        //Given
        long[] input = {2, 3, 0, 3, 99};
        long[] expected = {2, 3, 0, 6, 99};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test4() {
        //Given
        long[] input = {2, 4, 4, 5, 99, 0};
        long[] expected = {2, 4, 4, 5, 99, 9801};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test5() {
        //Given
        long[] input = {1, 1, 1, 4, 99, 5, 6, 0, 99};
        long[] expected = {30, 1, 1, 4, 2, 5, 6, 0, 99};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test6() {
        //Given
        long[] input = {1002, 4, 3, 4, 33};
        long[] expected = {1002, 4, 3, 4, 99};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test7() {
        //Given
        long[] input = {1101, 100, -1, 4, 0};
        long[] expected = {1101, 100, -1, 4, 99};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getMemory()).containsSequence(expected);
    }

    @Test
    public void test8() {
        //Given
        long[] input = {3, 0, 4, 0, 99};
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(expected);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_1() {
        //Given
        long[] input = {3, 9, 8, 9, 10, 9, 4, 9, 99, -1, 8};
        long inputValue = 8;
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_2() {
        //Given
        long[] input = {3, 9, 8, 9, 10, 9, 4, 9, 99, -1, 8};
        long inputValue = 4;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_3() {
        //Given
        long[] input = {3, 9, 8, 9, 10, 9, 4, 9, 99, -1, 8};
        long inputValue = 12;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test7_1() {
        //Given
        long[] input = {3, 9, 7, 9, 10, 9, 4, 9, 99, -1, 8};
        long inputValue = 8;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test7_2() {
        //Given
        long[] input = {3, 9, 7, 9, 10, 9, 4, 9, 99, -1, 8};
        long inputValue = 4;
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test7_3() {
        //Given
        long[] input = {3, 9, 7, 9, 10, 9, 4, 9, 99, -1, 8};
        long inputValue = 12;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }


    @Test
    public void test8_1_i() {
        //Given
        long[] input = {3, 3, 1108, -1, 8, 3, 4, 3, 99};
        long inputValue = 8;
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_2_i() {
        //Given
        long[] input = {3, 3, 1108, -1, 8, 3, 4, 3, 99};
        long inputValue = 4;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_3_i() {
        //Given
        long[] input = {3, 3, 1108, -1, 8, 3, 4, 3, 99};
        long inputValue = 12;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test7_1_i() {
        //Given
        long[] input = {3, 3, 1107, -1, 8, 3, 4, 3, 99};
        long inputValue = 8;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test7_2_i() {
        //Given
        long[] input = {3, 3, 1107, -1, 8, 3, 4, 3, 99};
        long inputValue = 4;
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test7_3_i() {
        //Given
        long[] input = {3, 3, 1107, -1, 8, 3, 4, 3, 99};
        long inputValue = 12;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test6_1() {
        //Given
        long[] input = {3, 12, 6, 12, 15, 1, 13, 14, 13, 4, 13, 99, -1, 0, 1, 9};
        long inputValue = 0;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test6_2() {
        //Given
        long[] input = {3, 12, 6, 12, 15, 1, 13, 14, 13, 4, 13, 99, -1, 0, 1, 9};
        long inputValue = 5;
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test6_1_i() {
        //Given
        long[] input = {3, 3, 1105, -1, 9, 1101, 0, 0, 12, 4, 12, 99, 1};
        long inputValue = 0;
        long expected = 0;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test6_2_i() {
        //Given
        long[] input = {3, 3, 1105, -1, 9, 1101, 0, 0, 12, 4, 12, 99, 1};
        long inputValue = 5;
        long expected = 1;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_1_g() {
        //Given
        long[] input = {3, 21, 1008, 21, 8, 20, 1005, 20, 22, 107, 8, 21, 20, 1006, 20, 31, 1106, 0, 36, 98, 0, 0, 1002, 21, 125, 20, 4, 20, 1105, 1, 46, 104, 999, 1105, 1, 46, 1101, 1000, 1, 20, 4, 20, 1105, 1, 46, 98, 99};
        long inputValue = 5;
        long expected = 999;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }


    @Test
    public void test8_2_g() {
        //Given
        long[] input = {3, 21, 1008, 21, 8, 20, 1005, 20, 22, 107, 8, 21, 20, 1006, 20, 31, 1106, 0, 36, 98, 0, 0, 1002, 21, 125, 20, 4, 20, 1105, 1, 46, 104, 999, 1105, 1, 46, 1101, 1000, 1, 20, 4, 20, 1105, 1, 46, 98, 99};
        long inputValue = 8;
        long expected = 1000;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test8_3_g() {
        //Given
        long[] input = {3, 21, 1008, 21, 8, 20, 1005, 20, 22, 107, 8, 21, 20, 1006, 20, 31, 1106, 0, 36, 98, 0, 0, 1002, 21, 125, 20, 4, 20, 1105, 1, 46, 104, 999, 1105, 1, 46, 1101, 1000, 1, 20, 4, 20, 1105, 1, 46, 98, 99};
        long inputValue = 10;
        long expected = 1001;
        service.setMemory(input);
        service.getIo().input.add(inputValue);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactly(expected);
    }

    @Test
    public void test9_1() {
        //Given
        long[] input = {109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99};
        long[] expected = {109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactlyElementsOf(Arrays.stream(expected).boxed().collect(Collectors.toList()));
    }

    @Test
    public void test9_2() {
        //Given
        long[] input = {1102, 34915192L, 34915192L, 7, 4, 7, 99, 0};
        long[] expected = {1125899906842624L};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        System.out.println(service.getIo().output);
//        Assertions.assertThat(service.getIo().output).containsExactlyElementsOf(Arrays.stream(expected).boxed().collect(Collectors.toList()));
    }

    @Test
    public void test9_3() {
        //Given
        long[] input = {104, 1125899906842624L, 99};
        long[] expected = {1125899906842624L};
        service.setMemory(input);

        //When
        service.execute();

        //Then
        Assertions.assertThat(service.getIo().output).containsExactlyElementsOf(Arrays.stream(expected).boxed().collect(Collectors.toList()));
    }

    //    @Test
    public void test9() throws Exception {
        //Given
        List<String> lines = lines();

        //When
        for (int noun = 0; noun < 100; noun++) {
            for (int verb = 0; verb < 100; verb++) {
                service.setNoun(noun);
                service.setVerb(verb);
                service.consume(lines);
                long result = service.getMemory()[0];
                if (result == 19690720) {
                    log.info("Noun = {}", noun);
                    log.info("Verb = {}", verb);
                    log.info("Res  = {}", (100 * noun + verb));
                }
            }
        }

        //Then
    }

    private List<String> lines() throws Exception {
        String input = "day2-1.txt";
        URI resource = this.getClass().getClassLoader().getResource("input/" + input).toURI();
        return Files.readAllLines(Paths.get(resource));
    }
}