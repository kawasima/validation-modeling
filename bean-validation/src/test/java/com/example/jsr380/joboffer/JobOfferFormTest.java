package com.example.jsr380.joboffer;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JobOfferFormTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private JobOfferForm baseForm(String type) {
        JobOfferForm form = new JobOfferForm();
        form.setTitle("Valid Title");
        form.setDescription("Valid Description");
        form.setJobOfferType(type);
        form.setOfferExpireDate(LocalDate.now().plusDays(30));
        return form;
    }

    @Nested
    @DisplayName("共通バリデーション")
    class CommonTests {
        @Test
        @DisplayName("タイトルが空の場合エラー")
        void blankTitle() {
            JobOfferForm form = baseForm("PROJECT");
            form.setTitle("");
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);
            form.setBudgetType("UNDECIDED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        }

        @Test
        @DisplayName("タイトルが100文字を超える場合エラー")
        void titleTooLong() {
            JobOfferForm form = baseForm("PROJECT");
            form.setTitle("あ".repeat(101));
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);
            form.setBudgetType("UNDECIDED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        }

        @Test
        @DisplayName("依頼タイプが不正の場合エラー")
        void invalidJobOfferType() {
            JobOfferForm form = baseForm("INVALID");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("jobOfferType"));
        }

        @Test
        @DisplayName("募集期限が過去の場合エラー")
        void pastExpireDate() {
            JobOfferForm form = baseForm("PROJECT");
            form.setOfferExpireDate(LocalDate.now().minusDays(1));
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);
            form.setBudgetType("UNDECIDED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("offerExpireDate"));
        }
    }

    @Nested
    @DisplayName("PROJECT")
    class ProjectTests {
        @Test
        @DisplayName("有効なPROJECT（Fixed/Range）")
        void validFixedRange() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(5L);
            form.setBudgetType("RANGE");
            form.setBudgetLowerBound(100000L);
            form.setBudgetUpperBound(500000L);

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("有効なPROJECT（Fixed/Limit）")
        void validFixedLimit() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);
            form.setBudgetType("LIMIT");
            form.setBudgetLimit(500000L);

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("有効なPROJECT（Fixed/Undecided）")
        void validFixedUndecided() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);
            form.setBudgetType("UNDECIDED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("有効なPROJECT（PER_HOUR）")
        void validPerHour() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("PER_HOUR");
            form.setNumberOfWorkers(3L);
            form.setHourlyRate("FROM_1000_TO_1500");
            form.setWorkHoursPerWeek(40L);
            form.setOfferDuration("ONE_MONTH_TO_THREE_MONTHS");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("settlementMethodがnullの場合エラー")
        void noSettlementMethod() {
            JobOfferForm form = baseForm("PROJECT");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("settlementMethod"));
        }

        @Test
        @DisplayName("FIXED時にbudgetTypeがnullの場合エラー")
        void fixedNoBudgetType() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("budgetType"));
        }

        @Test
        @DisplayName("RANGE時にlowerBoundがマイナスの場合エラー")
        void rangeLowerBoundNegative() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("FIXED");
            form.setNumberOfWorkers(3L);
            form.setBudgetType("RANGE");
            form.setBudgetLowerBound(-1000L);
            form.setBudgetUpperBound(500000L);

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("budgetLowerBound"));
        }

        @Test
        @DisplayName("PER_HOUR時にhourlyRateがnullの場合エラー")
        void perHourNoHourlyRate() {
            JobOfferForm form = baseForm("PROJECT");
            form.setSettlementMethod("PER_HOUR");
            form.setNumberOfWorkers(3L);
            form.setWorkHoursPerWeek(40L);
            form.setOfferDuration("ONE_MONTH_TO_THREE_MONTHS");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("hourlyRate"));
        }
    }

    @Nested
    @DisplayName("TASK")
    class TaskTests {
        @Test
        @DisplayName("有効なTASK（limited）")
        void validLimited() {
            JobOfferForm form = baseForm("TASK");
            form.setRatePerTaskUnit(100L);
            form.setNumberOfTaskUnits(1000L);
            form.setLimitTaskUnitsType("LIMITED");
            form.setLimitTaskUnitsValue(50L);

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("有効なTASK（unlimited）")
        void validUnlimited() {
            JobOfferForm form = baseForm("TASK");
            form.setRatePerTaskUnit(100L);
            form.setNumberOfTaskUnits(1000L);
            form.setLimitTaskUnitsType("UNLIMITED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("ratePerTaskUnitがnullの場合エラー")
        void noRatePerTaskUnit() {
            JobOfferForm form = baseForm("TASK");
            form.setNumberOfTaskUnits(1000L);
            form.setLimitTaskUnitsType("UNLIMITED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ratePerTaskUnit"));
        }

        @Test
        @DisplayName("ratePerTaskUnitが小さすぎる場合エラー")
        void ratePerTaskUnitTooLow() {
            JobOfferForm form = baseForm("TASK");
            form.setRatePerTaskUnit(3L);
            form.setNumberOfTaskUnits(1000L);
            form.setLimitTaskUnitsType("UNLIMITED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("ratePerTaskUnit"));
        }

        @Test
        @DisplayName("LIMITED時にlimitTaskUnitsValueがnullの場合エラー")
        void limitedNoValue() {
            JobOfferForm form = baseForm("TASK");
            form.setRatePerTaskUnit(100L);
            form.setNumberOfTaskUnits(1000L);
            form.setLimitTaskUnitsType("LIMITED");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("limitTaskUnitsValue"));
        }
    }

    @Nested
    @DisplayName("COMPETITION")
    class CompetitionTests {
        @Test
        @DisplayName("有効なCOMPETITION（standard）")
        void validStandard() {
            JobOfferForm form = baseForm("COMPETITION");
            form.setContractPriceType("STANDARD");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("有効なCOMPETITION（custom）")
        void validCustom() {
            JobOfferForm form = baseForm("COMPETITION");
            form.setContractPriceType("CUSTOM");
            form.setContractPriceValue(50000L);

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("contractPriceTypeがnullの場合エラー")
        void noContractPriceType() {
            JobOfferForm form = baseForm("COMPETITION");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("contractPriceType"));
        }

        @Test
        @DisplayName("CUSTOM時にcontractPriceValueがnullの場合エラー")
        void customNoValue() {
            JobOfferForm form = baseForm("COMPETITION");
            form.setContractPriceType("CUSTOM");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("contractPriceValue"));
        }

        @Test
        @DisplayName("contractPriceTypeが不正の場合エラー")
        void invalidContractPriceType() {
            JobOfferForm form = baseForm("COMPETITION");
            form.setContractPriceType("INVALID");

            Set<ConstraintViolation<JobOfferForm>> violations = validator.validate(form);

            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("contractPriceType"));
        }
    }
}
