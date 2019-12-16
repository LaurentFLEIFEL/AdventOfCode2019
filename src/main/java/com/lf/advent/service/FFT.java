package com.lf.advent.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.eclipse.collections.api.factory.Maps;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FFT implements LinesConsumer {

    @Setter
    private int nbrOfPhase = 100;
    @Setter
    private int nbrOfRepeat = 10_000;
    @Setter
    private boolean shouldOffset = true;

    private Map<Integer, IndexInfo> indexInfos = Maps.mutable.empty();
    private int offset;

    @Override
    public void consume(List<String> lines) {
        int[] initialInput = Arrays.stream(lines.get(0).split(""))
                                   .mapToInt(Integer::parseInt)
                                   .toArray();

        int[] output = execute(initialInput);

        int[] finalOutput = display(output);
        log.info("Output[8] = {}", Arrays.toString(finalOutput));
    }

    public int[] display(int[] output) {
        int[] copy = new int[8];
        System.arraycopy(output, offset, copy, 0, 8);
        return copy;
    }

    private int retrieveOffset(int[] initialInput) {
        int offset = 0;
        if (shouldOffset) {
            String indexS = Arrays.stream(Arrays.copyOf(initialInput, 7))
                                  .mapToObj(Integer::toString)
                                  .collect(Collectors.joining());

            offset = Integer.parseInt(indexS);
        }
        return offset;
    }

    public int[] execute(int[] origin) {
        log.info("Start duplicating");
        int[] initialInput = duplicate(origin, nbrOfRepeat);
        log.info("End duplicating");
        int length = initialInput.length;
        int[] output = Arrays.copyOf(initialInput, length);

        offset = retrieveOffset(initialInput);
        int minDigit = Math.max(offset, 0);
        log.info("{} indexes in total", output.length);
        log.info("Computing for {} indexes", output.length - minDigit + 1);

        for (int phase = 0; phase < nbrOfPhase; phase++) {
            log.info("Start phase#{}", phase);
            //for part 1
//            for (int index = minDigit; index < output.length; index++) {
//                int i = computeNthElement(output, index, origin.length);
//                int value = Math.abs(i) % 10;
//                output[index] = value;
//            }

            //for part 2 because the offset > input.length/2
            int previousSum = 0;
            for (int index = output.length - 1; index >= minDigit; index--) {
                int i = output[index] + previousSum;
                previousSum = i;
                int value = Math.abs(i) % 10;
                output[index] = value;
            }
            log.info("End phase#{}", phase);
            log.info("Output = {}", Arrays.toString(display(output)));

//            MapUtils.debugPrint(System.out, null, indexInfos);
        }
        return output;
    }

    private int computeNthElement(int[] input, int index, int initialLength) {
        IndexInfo indexInfo = indexInfos.computeIfAbsent(index, index1 -> IndexInfo.of(initialLength, index1, input.length));

        return sum(input, index, indexInfo);
    }

    private int sum(int[] input, int replicator, IndexInfo indexInfo) {
        int sum = 0;
        int remainingSum = 0;
        for (int index = replicator; index < Math.min(indexInfo.getLcm(), input.length) ; index++) {
            if (index == indexInfo.getRemaining()) {
                remainingSum = sum;
                if (indexInfo.getFactor() == 0) {
                    break;
                }
            }
            int i = input[index];
            int multiplyWithNext = multiplyWithNext(i, replicator, index);
            sum += multiplyWithNext;
        }
        if (indexInfo.getRemaining() == input.length) {
            remainingSum = sum;
        }
        return sum * indexInfo.getFactor() + remainingSum;
    }

    @Getter
    @ToString
    public static class IndexInfo {
        private int index;
        private int lcm;
        private int factor;

        private int remaining;
        public static IndexInfo of(int initialLength, int index, int inputLength) {
            IndexInfo indexInfo = new IndexInfo();
            indexInfo.index = index;
            if (4 * (index + 1) >= inputLength) {
                indexInfo.lcm = inputLength;
            } else {
                indexInfo.lcm = ArithmeticUtils.lcm(initialLength, 4 * (index + 1));
            }
            indexInfo.factor = inputLength/indexInfo.lcm;
            indexInfo.remaining = inputLength % indexInfo.lcm;

            return indexInfo;
        }

    }

    private static int[] duplicate(int[] input, int nbrOfRepeat) {
        int newLength = input.length * nbrOfRepeat;
        int[] result = Arrays.copyOf(input, newLength);
        for (int last = input.length; last != 0 && last < newLength; last <<= 1) {
            System.arraycopy(result, 0, result, last, Math.min(last << 1, newLength) - last);
        }

        return result;
    }

    private static final int[] initialPattern = {0, 1, 0, -1};

    public static int computeFactor(int replicationFactor, int index) {
        int computedIndex = ((index + 1) / (replicationFactor+1)) % 4;
        return initialPattern[computedIndex];
    }

    public int multiplyWithNext(int element, int replicationFactor, int index) {
        int next = computeFactor(replicationFactor, index);
        if (next == 0) {
            return 0;
        }
        if (next == 1) {
            return  element;
        }

        if (next == -1) {
            return -element;
        }

        return Integer.MAX_VALUE;
    }
}
