package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FuelCalculator implements LinesConsumer {
    @Override
    public void consume(List<String> lines) {
        int sum = lines.stream()
                       .mapToInt(Integer::parseInt)
                       .map(this::computeTotalFuel)
                       .sum();

        log.info("Sum of fuel is {}.", sum);
    }

    public int computeTotalFuel(int mass) {
        int aux = computeFuel(mass);
        MutableIntList fuels = IntLists.mutable.empty();
        while (aux > 0) {
            fuels.add(aux);
            aux = computeFuel(aux);
        }
        return (int) fuels.sum();
    }

    public int computeFuel(int mass) {
        int fuel = (mass / 3) - 2;
        return Math.max(fuel, 0);
    }
}
