package com.lf.advent.service;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
@Getter
@Setter
public class IntCode implements LinesConsumer {
    private static final Consumer<Parameters> DO_NOTHING = parameters -> {
    };

    private long[] memory;
    private int noun = 12;
    private int verb = 2;
    private IO io = new IO();
    private int address = 0;
    private int relativeBase = 0;

    @Override
    public void consume(List<String> lines) {
        memory = Arrays.stream(lines.get(0).split(","))
                       .mapToLong(Long::parseLong)
                       .toArray();

        io.input.add(2L);

//        memory[1] = noun;
//        memory[2] = verb;
        execute();
        log.info("memory[0] = {}", memory[0]);
        log.info("output = {}", io.output);
    }

    public Instruction execute() {
        memory = Arrays.copyOf(memory, 10_000);
        Instruction instruction = Instruction.of(memory[address]);
        while (instruction != Instruction.FINISH) {
//            log.info("Memory = {}", memory);
            Parameters parameters = Parameters.of(address, memory, io, relativeBase);
//            log.info("Executing instruction {}", instruction);
            instruction.execute(parameters);
            address = instruction.nextAddress(parameters);
//            log.info("address = {}", address);
            relativeBase = parameters.relativeBase;
//            log.info("relativeBase = {}", relativeBase);
            if (instruction == Instruction.OUTPUT) {
                return instruction;
            }
            instruction = Instruction.of(memory[address]);
        }

        return instruction;
    }

    enum Instruction {
        ADD(1,
            parameters -> parameters.address + 4,
            parameters -> parameters.updateThird(parameters.firstParameter() + parameters.secondParameter())),
        MULTIPLY(2,
                 parameters -> parameters.address + 4,
                 parameters -> parameters.updateThird(parameters.firstParameter() * parameters.secondParameter())),
        INPUT(3,
              parameters -> parameters.address + 2,
              parameters -> parameters.updateFirst(parameters.readInput())),
        OUTPUT(4,
               parameters -> parameters.address + 2,
               parameters -> parameters.writeOutput(parameters.firstParameter())),
        JUMP_IF_TRUE(5,
                     parameters -> (int) (parameters.firstParameter() != 0 ? parameters.secondParameter() : parameters.address + 3),
                     DO_NOTHING),
        JUMP_IF_FALSE(6,
                      parameters -> (int) (parameters.firstParameter() == 0 ? parameters.secondParameter() : parameters.address + 3),
                      DO_NOTHING),
        LESS_THAN(7,
                  parameters -> parameters.address + 4,
                  parameters -> {
                      int value = parameters.firstParameter() < parameters.secondParameter() ? 1 : 0;
                      parameters.updateThird(value);
                  }),
        EQUALS(8,
               parameters -> parameters.address + 4,
               parameters -> {
                   int value = parameters.firstParameter() == parameters.secondParameter() ? 1 : 0;
                   parameters.updateThird(value);
               }),
        ADJUST_RELATIVE_BASE(9,
                             parameters -> parameters.address + 2,
                             parameters -> {
                                parameters.setRelativeBase((int)(parameters.relativeBase + parameters.firstParameter()));
                             }
        ),
        FINISH(99,
               parameters -> 0,
               DO_NOTHING);

        private int code;
        @Getter
        private Function<Parameters, Integer> nextAddress;
        private Consumer<Parameters> apply;

        Instruction(int code, Function<Parameters, Integer> nextAddress, Consumer<Parameters> apply) {
            this.code = code;
            this.nextAddress = nextAddress;
            this.apply = apply;
        }

        public static Instruction of(long code) {
            return Arrays.stream(Instruction.values())
                         .filter(instruction -> instruction.code == (code % 100))
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Instruction " + code + " is not recognised"));
        }

        public void execute(Parameters parameters) {
            this.apply.accept(parameters);
        }

        public int nextAddress(Parameters parameters) {
            return this.nextAddress.apply(parameters);
        }
    }

    public static class IO {
        Deque<Long> input = new ArrayDeque<>();
        List<Long> output = Lists.mutable.empty();
    }

    @Builder
    public static class Parameters {
        int address;
        long[] memory;
        IO io;
        @Setter
        int relativeBase;

        private static final ImmutableIntIntMap divByIndex = IntIntMaps.mutable.empty()
                                                                               .withKeyValue(1, 100)
                                                                               .withKeyValue(2, 1000)
                                                                               .withKeyValue(3, 10000)
                                                                               .toImmutable();

        public static Parameters of(int address, long[] memory, IO io, int relativeBase) {
            return Parameters.builder()
                             .address(address)
                             .memory(memory)
                             .io(io)
                             .relativeBase(relativeBase)
                             .build();
        }

        public long readInput() {
            Long aLong = io.input.pollFirst();
//            log.info("read input = {}", aLong);
            return aLong;
        }

        public void writeOutput(long value) {
            io.output.add(value);
        }

        public long firstParameter() {
            return memory[computeAddress(1)];
        }

        public long secondParameter() {
            return memory[computeAddress(2)];
        }

        public long thirdParameter() {
            return memory[computeAddress(3)];
        }

        public void updateFirst(long value) {
            memory[computeAddress(1)] = value;
        }

        public void updateSecond(int value) {
            memory[computeAddress(2)] = value;
        }

        public void updateThird(long value) {
            memory[computeAddress(3)] = value;
        }

        private int computeAddress(int index) {
            long code = memory[address] / divByIndex.get(index) % 10;
            ModeParameter of = ModeParameter.of(code);
//            log.info("Mode {} ({})", of, code);
            long address1 = of.computeAddress(this, index);
//            log.info("Computed address = {}", address1);
            return (int)address1;
        }
    }

    enum ModeParameter {
        POSITION(0,
                 (parameters, index) -> parameters.memory[parameters.address + index]),
        IMMEDIATE(1,
                  (parameters, index) -> (long) parameters.address + index),
        RELATIVE(2,
                 (parameters, index) -> parameters.memory[parameters.address + index] + parameters.relativeBase);

        private int code;
        private BiFunction<Parameters, Integer, Long> addressComputer;

        ModeParameter(int code, BiFunction<Parameters, Integer, Long> addressComputer) {
            this.code = code;
            this.addressComputer = addressComputer;
        }

        public static ModeParameter of(long code) {
            return Arrays.stream(values())
                         .filter(modeParameter -> modeParameter.code == code)
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Mode parameter " + code + " not recognised."));
        }

        public long computeAddress(Parameters parameters, int index) {
            return this.addressComputer.apply(parameters, index);
        }
    }
}
