package com.example.jsr380.reserve;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ReserveTourFormTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("有効な入力の場合、バリデーションエラーなし")
    void validForm() {
        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(2);
        form.setChildCount(1);
        form.setRemarks("窓側希望");

        Set<ConstraintViolation<ReserveTourForm>> violations = validator.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("adultCountがnullの場合、エラー")
    void adultCountNull() {
        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(null);
        form.setChildCount(0);

        Set<ConstraintViolation<ReserveTourForm>> violations = validator.validate(form);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("adultCount"));
    }

    @Test
    @DisplayName("adultCountが6の場合、エラー")
    void adultCountExceedsMax() {
        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(6);
        form.setChildCount(0);

        Set<ConstraintViolation<ReserveTourForm>> violations = validator.validate(form);

        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("adultCount")
                        && v.getMessage().contains("5以下"));
    }

    @Test
    @DisplayName("childCountが負の場合、エラー")
    void childCountNegative() {
        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(1);
        form.setChildCount(-1);

        Set<ConstraintViolation<ReserveTourForm>> violations = validator.validate(form);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("childCount"));
    }

    @Test
    @DisplayName("remarksが80文字を超える場合、エラー")
    void remarksTooLong() {
        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(1);
        form.setChildCount(0);
        form.setRemarks("あ".repeat(81));

        Set<ConstraintViolation<ReserveTourForm>> violations = validator.validate(form);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("remarks"));
    }

    @Test
    @DisplayName("remarksがnullの場合、エラーなし")
    void remarksNull() {
        ReserveTourForm form = new ReserveTourForm();
        form.setAdultCount(1);
        form.setChildCount(0);
        form.setRemarks(null);

        Set<ConstraintViolation<ReserveTourForm>> violations = validator.validate(form);

        assertThat(violations).isEmpty();
    }
}
