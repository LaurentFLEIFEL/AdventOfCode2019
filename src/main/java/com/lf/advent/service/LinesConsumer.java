package com.lf.advent.service;

import java.util.List;

public interface LinesConsumer {

    void consume(List<String> lines);

    static LinesConsumer doNothing() {
        return lines -> {
            return;
        };
    }
}
