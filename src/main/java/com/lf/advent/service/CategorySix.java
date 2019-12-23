package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Maps;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class CategorySix implements LinesConsumer {

    public static final long NBR = 50;
    private Map<Long, Nic> nicByAddresses = Maps.mutable.empty();

    @Override
    public void consume(List<String> lines) {
        long[] memory = Arrays.stream(lines.get(0).split(","))
                              .mapToLong(Long::parseLong)
                              .toArray();

        Nat nat = new Nat(nicByAddresses);

        for (long address = 0; address < NBR; address++) {
            Nic nic = new Nic(address, memory, nicByAddresses, nat);
            nicByAddresses.put(address, nic);
        }

        ExecutorService executorService = Executors.newFixedThreadPool((int) NBR + 1);
        for (long address = 0; address < NBR; address++) {
            Nic nic = nicByAddresses.get(address);
            executorService.submit(nic::execute);
        }

        executorService.submit(() -> {
            try {
                nat.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static class Nat {
        private Map<Long, Nic> nicByAddresses;
        private long x;
        private long y;
        private long previousSentY = -1L;

        public Nat(Map<Long, Nic> nicByAddresses) {
            this.nicByAddresses = nicByAddresses;
        }

        public void execute() throws InterruptedException {
            Thread.sleep(4_000);
            while (!isNetworkIdle()) {
            }
            log.info("[NAT] Network idle. Tring to send x = {}, y = {} to address = 0", x, y);
            nicByAddresses.get(0L).addToInput(x, y);
            if (y == previousSentY) {
                log.info("[NAT] y = {} has been sent twice in a row", y);
            }
            previousSentY = y;
            execute();
        }

        private boolean isNetworkIdle() {
            return nicByAddresses.values().stream().allMatch(Nic::isIdle);
        }

        public void updatePacket(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Nic {
        private long address;
        private Map<Long, Nic> nicByAddresses;
        private IntCode intcode;
        private LocalDateTime lastOutput;
        private Nat nat;

        public Nic(long address, long[] memory, Map<Long, Nic> nicByAddresses, Nat nat) {
            this.address = address;
            this.nicByAddresses = nicByAddresses;
            this.nat = nat;
            intcode = new IntCode();
            intcode.setMemory(Arrays.copyOf(memory, memory.length));
            intcode.getIo().input = new NicInput();
        }

        public void addToInput(long x, long y) {
            intcode.getIo().input.add(x);
            intcode.getIo().input.add(y);
        }

        public boolean isIdle() {
            if (lastOutput == null) {
                return true;
            }
            LocalDateTime now = LocalDateTime.now();
            return Duration.between(lastOutput, now).toMillis() > 2_000;
        }

        public void execute() {
            IntCode.Instruction signal = null;
            List<Long> output = intcode.getIo().output;
            intcode.getIo().input.add(address);
            while (signal != IntCode.Instruction.FINISH) {
                signal = intcode.execute();
                signal = intcode.execute();
                signal = intcode.execute();

                Long adressTo = output.get(output.size() - 3);
                Long x = output.get(output.size() - 2);
                Long y = output.get(output.size() - 1);
                Nic destination = nicByAddresses.get(adressTo);

                if (destination == null) {
//                    log.info("[{}] Tring to send x = {}, y = {} to address = {}", address, x, y, adressTo);
                    if (adressTo == 255L) {
                        nat.updatePacket(x, y);
                    }
                } else {
                    destination.addToInput(x, y);
                }

                lastOutput = LocalDateTime.now();
            }
        }

        private static class NicInput extends ArrayDeque<Long> {
            @Override
            public Long pollFirst() {
                if (this.isEmpty()) {
                    return -1L;
                }
                return super.pollFirst();
            }
        }
    }
}
