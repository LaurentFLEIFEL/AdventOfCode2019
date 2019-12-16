package com.lf.advent.service;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class FFTTest {

    @Test
    public void test_1_1() {
        //Given
        int[] input = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] expectedResult = {0, 1, 0, 2, 9, 4, 9, 8};
        int nbrOfPhase = 4;
        int nbrOfRepeat = 1;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(false);

        //When
        int[] actualResult = service.execute(input);

        //Then
        Assertions.assertThat(actualResult).containsExactly(expectedResult);
    }

    @Test
    public void test_1_2() {
        //Given
        int[] input = {8, 0, 8, 7, 1, 2, 2, 4, 5, 8, 5, 9, 1, 4, 5, 4, 6, 6, 1, 9, 0, 8, 3, 2, 1, 8, 6, 4, 5, 5, 9, 5};
        int[] expectedResult = {2, 4, 1, 7, 6, 1, 7, 6};
        int nbrOfPhase = 100;
        int nbrOfRepeat = 1;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(false);

        //When
        int[] actualResult = service.execute(input);

        //Then
        Assertions.assertThat(actualResult).containsSequence(expectedResult);
    }

    @Test
    public void test_1_3() {
        //Given
        int[] input = {1, 9, 6, 1, 7, 8, 0, 4, 2, 0, 7, 2, 0, 2, 2, 0, 9, 1, 4, 4, 9, 1, 6, 0, 4, 4, 1, 8, 9, 9, 1, 7};
        int[] expectedResult = {7, 3, 7, 4, 5, 4, 1, 8};
        int nbrOfPhase = 100;
        int nbrOfRepeat = 1;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(false);

        //When
        int[] actualResult = service.execute(input);

        //Then
        Assertions.assertThat(actualResult).containsSequence(expectedResult);
    }

    @Test
    public void test_1_4() {
        //Given
        int[] input = {6, 9, 3, 1, 7, 1, 6, 3, 4, 9, 2, 9, 4, 8, 6, 0, 6, 3, 3, 5, 9, 9, 5, 9, 2, 4, 3, 1, 9, 8, 7, 3};
        int[] expectedResult = {5, 2, 4, 3, 2, 1, 3, 3};
        int nbrOfPhase = 100;
        int nbrOfRepeat = 1;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(false);

        //When
        int[] actualResult = service.execute(input);

        //Then
        Assertions.assertThat(actualResult).containsSequence(expectedResult);
    }

    @Test
    public void test_2_1() {
        //Given
        int[] input = {0, 3, 0, 3, 6, 7, 3, 2, 5, 7, 7, 2, 1, 2, 9, 4, 4, 0, 6, 3, 4, 9, 1, 5, 6, 5, 4, 7, 4, 6, 6, 4};
        int[] expectedResult = {8, 4, 4, 6, 2, 0, 2, 6};
        int nbrOfPhase = 100;
        int nbrOfRepeat = 10000;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(true);

        //When
        int[] aux = service.execute(input);
        int[] actualResult = service.display(aux);

        //Then
        Assertions.assertThat(actualResult).containsExactly(expectedResult);
    }

    @Test
    public void test_2_2() {
        //Given
        int[] input = {0,2,9,3,5,1,0,9,6,9,9,9,4,0,8,0,7,4,0,7,5,8,5,4,4,7,0,3,4,3,2,3};
        int[] expectedResult = {7,8,7,2,5,2,7,0};
        int nbrOfPhase = 100;
        int nbrOfRepeat = 10000;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(true);

        //When
        int[] aux = service.execute(input);
        int[] actualResult = service.display(aux);

        //Then
        Assertions.assertThat(actualResult).containsExactly(expectedResult);
    }

    @Test
    public void test_2_3() {
        //Given
        int[] input = {0,3,0,8,1,7,7,0,8,8,4,9,2,1,9,5,9,7,3,1,1,6,5,4,4,6,8,5,0,5,1,7};
        int[] expectedResult = {5,3,5,5,3,7,3,1};
        int nbrOfPhase = 100;
        int nbrOfRepeat = 10000;
        FFT service = new FFT();
        service.setNbrOfPhase(nbrOfPhase);
        service.setNbrOfRepeat(nbrOfRepeat);
        service.setShouldOffset(true);

        //When
        int[] aux = service.execute(input);
        int[] actualResult = service.display(aux);

        //Then
        Assertions.assertThat(actualResult).containsExactly(expectedResult);
    }

    @Test
    public void name() {
        //Given

        //When
        ArithmeticUtils.lcm(650, 23902712);

        //Then
    }
}