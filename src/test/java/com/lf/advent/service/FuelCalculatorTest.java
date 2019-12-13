package com.lf.advent.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class FuelCalculatorTest {

    private FuelCalculator fuelCalculator;

    @Before
    public void setUp() {
        fuelCalculator = new FuelCalculator();
    }

    @Test
    public void test_12_2() {
        //Given
        int mass = 12;
        int expectedFuel = 2;

        //When
        int actualFuel = fuelCalculator.computeFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }

    @Test
    public void test_14_2() {
        //Given
        int mass = 14;
        int expectedFuel = 2;

        //When
        int actualFuel = fuelCalculator.computeFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }

    @Test
    public void test_1969_654() {
        //Given
        int mass = 1969;
        int expectedFuel = 654;

        //When
        int actualFuel = fuelCalculator.computeFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }

    @Test
    public void test_100756_33583() {
        //Given
        int mass = 100756;
        int expectedFuel = 33583;

        //When
        int actualFuel = fuelCalculator.computeFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }

    @Test
    public void test2_14_2() {
        //Given
        int mass = 14;
        int expectedFuel = 2;

        //When
        int actualFuel = fuelCalculator.computeTotalFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }

    @Test
    public void test2_1969_966() {
        //Given
        int mass = 1969;
        int expectedFuel = 966;

        //When
        int actualFuel = fuelCalculator.computeTotalFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }

    @Test
    public void test2_100756_50346() {
        //Given
        int mass = 100756;
        int expectedFuel = 50346;

        //When
        int actualFuel = fuelCalculator.computeTotalFuel(mass);

        //Then
        Assertions.assertThat(actualFuel).isEqualTo(expectedFuel);
    }
}