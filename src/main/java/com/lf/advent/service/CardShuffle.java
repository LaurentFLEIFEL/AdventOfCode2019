package com.lf.advent.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongUnaryOperator;

@Service
@Slf4j
public class CardShuffle implements LinesConsumer {

    public static final long MAX_ITERATION = 101741582076661L;
    @Setter
    private long length = 119315717514047L;
    //    private long length = 10007;
    private long index = 2020;

    @Override
    public void consume(List<String> lines) {
        LongUnaryOperator indexComputer = LongUnaryOperator.identity();
        MutableList<Operation> operations = Lists.mutable.empty();

        for (String line : lines) {
            Operation operation = Operation.of(line, length);
            operations.add(operation);
            indexComputer = indexComputer.andThen(operation);
        }

        long count = 1;
        long computedIndex = indexComputer.applyAsLong(index);

        log.info("Card {} = {}", index, computedIndex);
        log.info("Count   = {}", count);

        BigInteger coeffA = BigInteger.ONE;
        BigInteger coeffB = BigInteger.ZERO;
        for (Operation operation : operations.reverseThis()) {
            coeffA = operation.retrieveReverseA(coeffA);
            coeffB = operation.retrieveReverseB(coeffB);
        }

//        log.info("Card {} = {}", index, (index * coeffA + coeffB )%length + length);

        BigInteger bigL = BigInteger.valueOf(length);
        BigInteger bigIter = BigInteger.valueOf(MAX_ITERATION);
        BigInteger bigIndex = BigInteger.valueOf(index);
        BigInteger bigA = coeffA;
        BigInteger bigB = coeffB;

        BigInteger result = bigIndex.multiply(bigA).add(bigB).mod(bigL);
        log.info("Card {} = {}", index, result);

        BigInteger APowIter = bigA.modPow(bigIter, bigL);
        BigInteger inverse = bigA.add(BigInteger.ONE.negate()).modInverse(bigL);
        result = APowIter.multiply(bigIndex).add(bigB.multiply(APowIter.add(BigInteger.ONE.negate())).multiply(inverse)).mod(bigL);
        log.info("Card {} = {}", index, result);
    }

    public enum ShuffleOperation {
        DEAL_WITH_NEW_STACK("deal into new stack",
                (length, index, parameter) -> length - index - 1,
                (length, previousA, parameter) -> -previousA % length,
                (length, previousB, parameter) -> (-previousB - 1) % length,
                (length, previousA, parameter) -> previousA.negate().mod(length),
                (length, previousB, parameter) -> previousB.negate().add(BigInteger.ONE.negate()).mod(length)),
        DEAL_WITH_INCREMENT("deal with increment",
                (length, index, parameter) -> (index * parameter) % length,
                (length, previousA, parameter) -> (previousA * parameter) % length,
                (length, previousB, parameter) -> (previousB * parameter) % length,
                (length, previousA, parameter) -> previousA.multiply(parameter.modInverse(length)).mod(length),
                (length, previousB, parameter) -> previousB.multiply(parameter.modInverse(length)).mod(length)),
        CUT("cut",
                (length, index, parameter) -> (index - parameter) % length,
                (length, previousA, parameter) -> previousA % length,
                (length, previousB, parameter) -> (previousB - parameter) % length,
                (length, previousA, parameter) -> previousA.mod(length),
                (length, previousB, parameter) -> previousB.add(parameter).mod(length));

        private String label;
        private TriLongFunction shuffler;
        private TriLongFunction getA;
        private TriLongFunction getB;
        private TriEndoFunction<BigInteger> getReverseA;
        private TriEndoFunction<BigInteger> getReverseB;

        ShuffleOperation(String label, TriLongFunction shuffler, TriLongFunction getA, TriLongFunction getB, TriEndoFunction<BigInteger> getReverseA,
                         TriEndoFunction<BigInteger> getReverseB) {
            this.label = label;
            this.shuffler = shuffler;
            this.getA = getA;
            this.getB = getB;
            this.getReverseA = getReverseA;
            this.getReverseB = getReverseB;
        }

        public static ShuffleOperation of(String line) {
            return Arrays.stream(values())
                         .filter(shuffleOperation -> line.startsWith(shuffleOperation.label))
                         .findAny()
                         .orElseThrow(() -> new IllegalArgumentException("Shuffle operation " + line + " is not recognised."));
        }

        public int retrieveParameter(String line) {
            String trim = line.substring(label.length()).trim();
            if (StringUtils.isEmpty(trim)) {
                return 0;
            }
            return Integer.parseInt(trim);
        }

        public long doShuffle(long length, long index, long parameter) {
            return this.shuffler.apply(length, index, parameter);
        }

        public long retrieveA(long length, long previousA, long parameter) {
            return this.getA.apply(length, previousA, parameter);
        }

        public long retrieveB(long length, long previousB, long parameter) {
            return this.getB.apply(length, previousB, parameter);
        }

        public BigInteger retrieveReverseA(BigInteger length, BigInteger previousA, BigInteger parameter) {
            return this.getReverseA.apply(length, previousA, parameter);
        }

        public BigInteger retrieveReverseB(BigInteger length, BigInteger previousA, BigInteger parameter) {
            return this.getReverseB.apply(length, previousA, parameter);
        }
    }

    @FunctionalInterface
    public interface TriLongFunction {
        long apply(long a, long b, long c);
    }

    @FunctionalInterface
    public interface TriEndoFunction<T> {
        T apply(T a, T b, T c);
    }

    public static class Operation implements LongUnaryOperator {
        private ShuffleOperation shuffleOperation;
        private int parameter;
        private long length;

        public static Operation of(String line, long length) {
            Operation operation = new Operation();
            operation.shuffleOperation = ShuffleOperation.of(line);
            operation.parameter = operation.shuffleOperation.retrieveParameter(line);
            operation.length = length;
            return operation;
        }

        @Override
        public long applyAsLong(long value) {
            return shuffleOperation.doShuffle(length, value, parameter);
        }

        public long retrieveA(long previousA) {
            return shuffleOperation.retrieveA(length, previousA, parameter);
        }

        public long retrieveB(long previousB) {
            return shuffleOperation.retrieveB(length, previousB, parameter);
        }

        public BigInteger retrieveReverseA(BigInteger previousA) {
            return shuffleOperation.retrieveReverseA(BigInteger.valueOf(length), previousA, BigInteger.valueOf(parameter));
        }

        public BigInteger retrieveReverseB(BigInteger previousB) {
            return shuffleOperation.retrieveReverseB(BigInteger.valueOf(length), previousB, BigInteger.valueOf(parameter));
        }
    }
}
