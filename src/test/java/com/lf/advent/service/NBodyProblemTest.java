package com.lf.advent.service;

import com.lf.advent.service.NBodyProblem.Body;
import org.eclipse.collections.api.factory.Lists;
import org.junit.Test;

import java.util.List;

public class NBodyProblemTest {

    @Test
    public void test1() {
        //Given
        long nbrOfSteps = 3000;
        List<Body> bodies = Lists.mutable.of(Body.of("A", -1, 0, 2),
                                             Body.of("B", 2, -10, -7),
                                             Body.of("C", 4, -8, 8),
                                             Body.of("D", 3, 5, -1));

        //When
        NBodyProblem service = new NBodyProblem();
        service.setNbrOfSteps(nbrOfSteps);
        service.setBodies(bodies);
        service.execute();
        //Then
    }

    @Test
    public void test2() {
        //Given
        long nbrOfSteps = 1_000_000L;
        List<Body> bodies = Lists.mutable.of(Body.of("A", -8, -10, 0),
                                             Body.of("B", 5, 5, 10),
                                             Body.of("C", 2, -7, 3),
                                             Body.of("D", 9, -8, -3));

        //When
        NBodyProblem service = new NBodyProblem();
        service.setNbrOfSteps(nbrOfSteps);
        service.setBodies(bodies);
        service.execute();
        //Then
    }

    @Test
    public void test() {
        //Given
        long nbrOfSteps = 1_000_000L;
        List<Body> bodies = Lists.mutable.of(Body.of("A", 9, 13, -8),
                                             Body.of("B", -3, 16, -17),
                                             Body.of("C", -4, 11, -10),
                                             Body.of("D", 0, -2, -2));

        //When
        NBodyProblem service = new NBodyProblem();
        service.setNbrOfSteps(nbrOfSteps);
        service.setBodies(bodies);
        service.execute();
        //Then
    }
}