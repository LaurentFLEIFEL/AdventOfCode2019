package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
public class ThrustAmplifierTest {

    ThrustAmplifier service = new ThrustAmplifier();

    @Test
    public void test1() {
        //Given
        List<String> lines = Lists.mutable.of("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0");
        List<Long> phases = Lists.mutable.of(4L, 3L, 2L, 1L, 0L);

        service.setPhaseSettings(phases);

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getOutput()).isEqualTo(43210);
    }

    @Test
    public void test2() {
        //Given
        List<String> lines = Lists.mutable.of("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0");
        List<Long> phases = Lists.mutable.of(0L, 1L, 2L, 3L, 4L);

        service.setPhaseSettings(phases);

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getOutput()).isEqualTo(54321);
    }

    @Test
    public void test3() {
        //Given
        List<String> lines = Lists.mutable.of("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0");
        List<Long> phases = Lists.mutable.of(1L, 0L, 4L, 3L, 2L);

        service.setPhaseSettings(phases);

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getOutput()).isEqualTo(65210);
    }

    @Test
    public void test4() {
        //Given
        List<String> lines = Lists.mutable.of("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5");
        List<Long> phases = Lists.mutable.of(9L, 8L, 7L, 6L, 5L);

        service.setPhaseSettings(phases);

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getOutput()).isEqualTo(139629729);
    }

    @Test
    public void test5() {
        //Given
        List<String> lines = Lists.mutable.of("3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10");
        List<Long> phases = Lists.mutable.of(9L, 7L, 8L, 5L, 6L);

        service.setPhaseSettings(phases);

        //When
        service.consume(lines);

        //Then
        Assertions.assertThat(service.getOutput()).isEqualTo(18216);
    }

    @Test
    public void test() throws Exception {
        //Given
        List<String> lines = lines();
        PermutationIterator<Long> permutationIterator = new PermutationIterator<>(Lists.mutable.of(5L, 6L, 7L, 8L, 9L));
        Iterable<List<Long>> it = () -> permutationIterator;

        //When
        long max = StreamSupport.stream(it.spliterator(), false)
                                .mapToLong(phases -> {
                                    service.setPhaseSettings(phases);
                                    service.consume(lines);
                                    return service.getOutput();
                                }).max()
                                .orElse(0);

        //Then
        log.info("Output = {}", max);
    }

    private List<String> lines() throws Exception {
        String input = "day7-1.txt";
        URI resource = this.getClass().getClassLoader().getResource("input/" + input).toURI();
        return Files.readAllLines(Paths.get(resource));
    }
}