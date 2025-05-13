package com.example.yavi.reserve;

import am.ik.yavi.core.Validated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReserveTourInputTest {
    @Test
    void adultCountIsNotNumber() {
        Validated<ReserveTourInput> validated = ReserveTourInput.parse("123", "abc", "0", "memo");
        assertThat(validated.isValid()).isFalse();
    }
    @Test
    void adultCountIs6() {
        Validated<ReserveTourInput> validated = ReserveTourInput.parse("123", "6", "0", "memo");
        assertThat(validated.isValid()).isFalse();
        assertThat(validated.errors().getFirst().name()).isEqualTo("adultCount");
        System.out.println(validated.errors().getFirst().message());
    }

}