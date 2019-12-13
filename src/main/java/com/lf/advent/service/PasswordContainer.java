package com.lf.advent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

@Service
@Slf4j
public class PasswordContainer implements LinesConsumer {
    @Override
    public void consume(List<String> lines) {
        int min = Integer.parseInt(lines.get(0));
        int max = Integer.parseInt(lines.get(1));

        long count = IntStream.range(min, max + 1)
                              .mapToObj(this::comply)
                              .filter(comply -> comply)
                              .count();

        log.info("Count = {}", count);
    }

    public boolean comply(int value) {
        return Arrays.stream(Criteria.values())
                     .allMatch(criteria -> criteria.test.test(value));
    }

    enum Criteria {
        ADJACENT(value -> {
            String s = Integer.toString(value);
            char previous = 'a';
            char current;
            for (int i = 0; i < s.length(); i++) {
                current = s.charAt(i);
                if (current == previous && (i < 2 || s.charAt(i - 2) != current) && (i == s.length() - 1 || s.charAt(i + 1) != current))
                    return true;
                previous = current;
            }
            return false;
        }),
        INCREASE(value -> {
            String s = Integer.toString(value);
            char previous = '0';
            char current;
            for (int i = 0; i < s.length(); i++) {
                current = s.charAt(i);
                if (current < previous)
                    return false;
                previous = current;
            }
            return true;
        });

        private IntPredicate test;

        Criteria(IntPredicate test) {
            this.test = test;
        }
    }
}
