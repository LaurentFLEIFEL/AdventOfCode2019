package com.lf.advent.service;

import org.assertj.core.api.Assertions;
import org.eclipse.collections.api.factory.Lists;
import org.junit.Test;

import java.util.List;

public class PasswordContainerTest {
    private PasswordContainer passwordContainer = new PasswordContainer();

    @Test
    public void test1() {
        //Given
        int value = 111111;

        //When

        //Then
        Assertions.assertThat(passwordContainer.comply(value)).isFalse();
    }

    @Test
    public void test2() {
        //Given
        int value = 223450;

        //When

        //Then
        Assertions.assertThat(passwordContainer.comply(value)).isFalse();
    }

    @Test
    public void test3() {
        //Given
        int value = 123789;

        //When

        //Then
        Assertions.assertThat(passwordContainer.comply(value)).isFalse();
    }

    @Test
    public void test4() {
        //Given
        int value = 112233;

        //When

        //Then
        Assertions.assertThat(passwordContainer.comply(value)).isTrue();
    }

    @Test
    public void test5() {
        //Given
        int value = 123444;

        //When

        //Then
        Assertions.assertThat(passwordContainer.comply(value)).isFalse();
    }

    @Test
    public void test6() {
        //Given
        int value = 111122;

        //When

        //Then
        Assertions.assertThat(passwordContainer.comply(value)).isTrue();
    }

    @Test
    public void test7() {
        //Given
        List<String> lines = Lists.fixedSize.of("145852", "616942");

        //When
        passwordContainer.consume(lines);

        //Then
    }
}