package com.example.yavi.joboffer.domain;

import am.ik.yavi.core.Validated;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JobOfferTest {

    @Nested
    @DisplayName("PROJECT")
    class ProjectTests {
        @Test
        @DisplayName("有効なプロジェクト案件（Fixed/Range）")
        void validFixedRange() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "project",
                    "title", "valid title",
                    "description", "valid description",
                    "settlement", Map.of(
                            "type", "fixed",
                            "budget", Map.of(
                                    "type", "range",
                                    "lowerBound", 1000,
                                    "upperBound", 2000
                            ),
                            "numberOfWorkers", 5L
                    ),
                    "offerExpireDate", "2026-10-10"
            ));
            assertThat(validated.isValid()).isTrue();
            assertThat(validated.value()).isInstanceOf(ProjectJobOffer.class);
        }

        @Test
        @DisplayName("lowerBoundがマイナスの場合エラー")
        void minusLowerBound() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "project",
                    "title", "valid title",
                    "description", "valid description",
                    "settlement", Map.of(
                            "type", "fixed",
                            "budget", Map.of(
                                    "type", "range",
                                    "lowerBound", -1000,
                                    "upperBound", 2000
                            ),
                            "numberOfWorkers", 5L
                    ),
                    "offerExpireDate", "2026-10-10"
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("lowerBound"));
        }

        @Test
        @DisplayName("titleが空の場合エラー")
        void blankTitle() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "project",
                    "title", "",
                    "description", "valid description",
                    "settlement", Map.of(
                            "type", "fixed",
                            "budget", Map.of(
                                    "type", "range",
                                    "lowerBound", 1000,
                                    "upperBound", 2000
                            ),
                            "numberOfWorkers", 5L
                    ),
                    "offerExpireDate", "2026-10-10"
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("title"));
        }
    }

    @Nested
    @DisplayName("TASK")
    class TaskTests {
        @Test
        @DisplayName("有効なタスク案件（limited）")
        void validLimited() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 100L,
                    "numberOfTaskUnits", 1000L,
                    "limitTaskUnitsPerWorker", Map.of(
                            "type", "limited",
                            "limit", 50L
                    )
            ));
            assertThat(validated.isValid()).isTrue();
            assertThat(validated.value()).isInstanceOf(TaskJobOffer.class);
        }

        @Test
        @DisplayName("有効なタスク案件（unlimited）")
        void validUnlimited() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 100L,
                    "numberOfTaskUnits", 1000L,
                    "limitTaskUnitsPerWorker", Map.of(
                            "type", "unlimited"
                    )
            ));
            assertThat(validated.isValid()).isTrue();
        }

        @Test
        @DisplayName("ratePerTaskUnitが5以下の場合エラー")
        void ratePerTaskUnitTooLow() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 3L,
                    "numberOfTaskUnits", 1000L,
                    "limitTaskUnitsPerWorker", Map.of(
                            "type", "unlimited"
                    )
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("ratePerTaskUnit"));
        }

        @Test
        @DisplayName("numberOfTaskUnitsが1以下の場合エラー")
        void numberOfTaskUnitsTooLow() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 100L,
                    "numberOfTaskUnits", 0L,
                    "limitTaskUnitsPerWorker", Map.of(
                            "type", "unlimited"
                    )
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("numberOfTaskUnits"));
        }
    }

    @Nested
    @DisplayName("COMPETITION")
    class CompetitionTests {
        @Test
        @DisplayName("有効なコンペ案件（standard）")
        void validStandard() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of(
                            "type", "standard"
                    )
            ));
            assertThat(validated.isValid()).isTrue();
            assertThat(validated.value()).isInstanceOf(CompetitionJobOffer.class);
        }

        @Test
        @DisplayName("有効なコンペ案件（custom）")
        void validCustom() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of(
                            "type", "custom",
                            "value", 50000L
                    )
            ));
            assertThat(validated.isValid()).isTrue();
            CompetitionJobOffer offer = (CompetitionJobOffer) validated.value();
            assertThat(offer.contractPrice()).isInstanceOf(ContractPrice.Custom.class);
        }

        @Test
        @DisplayName("contractPriceのtypeが不正な場合エラー")
        void invalidContractPriceType() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of(
                            "type", "invalid"
                    )
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("type"));
        }

        @Test
        @DisplayName("custom valueが0以下の場合エラー")
        void customValueZero() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of(
                            "type", "custom",
                            "value", 0L
                    )
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("value"));
        }
    }

    @Nested
    @DisplayName("共通バリデーション")
    class CommonTests {
        @Test
        @DisplayName("typeが不正な場合エラー")
        void invalidType() {
            Validated<JobOffer> validated = JobOffer.mapValidator.validate(Map.of(
                    "type", "invalid",
                    "title", "Title",
                    "description", "Desc",
                    "offerExpireDate", "2026-12-31"
            ));
            assertThat(validated.isValid()).isFalse();
            assertThat(validated.errors()).anyMatch(e -> e.name().equals("type"));
        }
    }
}
