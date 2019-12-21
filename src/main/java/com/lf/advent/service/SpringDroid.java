package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpringDroid implements LinesConsumer {

    private String ROUTINE =
            "NOT A J\n" +
                    "NOT B T\n" +
                    "OR J T\n" +
                    "NOT C J\n" +
                    "OR J T\n" +

                    "NOT E J\n" +
                    "AND H J\n" +
                    "OR E J\n" +
                    "AND J T\n" +

                    "NOT D J\n" +
                    "AND D J\n" +
                    "OR D J\n" +

                    "AND T J\n" +
                    "RUN\n";

    @Override
    public void consume(List<String> lines) {
        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        IntCode service = new IntCode();
        service.setMemory(Arrays.copyOf(memory, memory.length));
        IntCode.Instruction signal = null;
        List<Long> output = service.getIo().output;
        Deque<Long> input = service.getIo().input;
        initializeInput(input);

        while (signal != IntCode.Instruction.FINISH) {
            signal = service.execute();
            Long aLong = output.get(output.size() - 1);
            if (aLong > 1000) {
                System.out.println(aLong);
            }
            char tile = (char) aLong.intValue();
            System.out.print(tile);
        }
    }

    private void initializeInput(Deque<Long> input) {
        input.addAll(ROUTINE.chars()
                            .mapToLong(i -> (long) i)
                            .boxed()
                            .collect(Collectors.toList()));
    }
}
