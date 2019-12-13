package com.lf.advent.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ThrustAmplifier implements LinesConsumer {

    @Setter
    private List<Long> phaseSettings;
    @Getter
    private long output;

    @Override
    public void consume(List<String> lines) {

        List<IntCode> services = Lists.mutable.empty();

        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        for (int i = 0; i < 5; i++) {
            IntCode service = new IntCode();
            service.getIo().input.add(phaseSettings.get(i));
            service.setMemory(Arrays.copyOf(memory, memory.length));
            services.add(service);
        }

        IntCode.Instruction signal;
        int index = 0;
        long previousOutput = 0;
        while (true) {
            IntCode amplifier = services.get(index % 5);
            amplifier.getIo().input.add(previousOutput);
            signal = amplifier.execute();
            if (signal == IntCode.Instruction.FINISH) {
                break;
            }
            previousOutput = amplifier.getIo().output.get(amplifier.getIo().output.size() - 1);
            index++;
            log.info("Signal {} for amplifier {} with output {}", signal, (index % 5), previousOutput);
        }

        output = previousOutput;
        log.info("Output = {}", output);
    }
}
