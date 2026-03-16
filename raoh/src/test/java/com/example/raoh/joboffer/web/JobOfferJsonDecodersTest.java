package com.example.raoh.joboffer.web;

import com.example.raoh.joboffer.data.*;
import net.unit8.raoh.Err;
import net.unit8.raoh.Ok;
import net.unit8.raoh.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static com.example.raoh.joboffer.web.JobOfferJsonDecoders.JOB_OFFER_DECODER;
import static org.assertj.core.api.Assertions.assertThat;

class JobOfferJsonDecodersTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static JsonNode json(Map<String, Object> map) {
        return objectMapper.valueToTree(map);
    }

    @Nested
    @DisplayName("PROJECT")
    class ProjectTests {
        @Test
        @DisplayName("Fixed/Range: 有効な入力")
        void validFixedRange() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "project",
                    "title", "Web Dev",
                    "description", "Build a website",
                    "offerExpireDate", "2026-12-31",
                    "settlement", Map.of(
                            "type", "fixed",
                            "numberOfWorkers", 5,
                            "budget", Map.of(
                                    "type", "range",
                                    "lowerBound", 100000,
                                    "upperBound", 500000
                            )
                    )
            )));
            assertThat(result).isInstanceOf(Ok.class);
            assertThat(((Ok<JobOffer>) result).value()).isInstanceOf(ProjectJobOffer.class);
            ProjectJobOffer p = (ProjectJobOffer) ((Ok<JobOffer>) result).value();
            assertThat(p.settlement()).isInstanceOf(FixedSettlement.class);
            assertThat(((FixedSettlement) p.settlement()).budget()).isInstanceOf(RangeBudget.class);
        }

        @Test
        @DisplayName("Fixed/Limit: 有効な入力")
        void validFixedLimit() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "project",
                    "title", "Web Dev",
                    "description", "Build a website",
                    "offerExpireDate", "2026-12-31",
                    "settlement", Map.of(
                            "type", "fixed",
                            "numberOfWorkers", 3,
                            "budget", Map.of(
                                    "type", "limit",
                                    "limit", 500000
                            )
                    )
            )));
            assertThat(result).isInstanceOf(Ok.class);
            FixedSettlement fs = (FixedSettlement) ((ProjectJobOffer) ((Ok<JobOffer>) result).value()).settlement();
            assertThat(fs.budget()).isInstanceOf(LimitBudget.class);
        }

        @Test
        @DisplayName("Fixed/Undecided: 有効な入力")
        void validFixedUndecided() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "project",
                    "title", "Web Dev",
                    "description", "Build a website",
                    "offerExpireDate", "2026-12-31",
                    "settlement", Map.of(
                            "type", "fixed",
                            "numberOfWorkers", 3,
                            "budget", Map.of("type", "undecided")
                    )
            )));
            assertThat(result).isInstanceOf(Ok.class);
            FixedSettlement fs = (FixedSettlement) ((ProjectJobOffer) ((Ok<JobOffer>) result).value()).settlement();
            assertThat(fs.budget()).isInstanceOf(UndecidedBudget.class);
        }

        @Test
        @DisplayName("PerHour: 有効な入力")
        void validPerHour() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "project",
                    "title", "Consulting",
                    "description", "Technical consulting",
                    "offerExpireDate", "2026-12-31",
                    "settlement", Map.of(
                            "type", "per_hour",
                            "numberOfWorkers", 2,
                            "hourlyRate", "FROM_1000_TO_1500",
                            "workHoursPerWeek", 40,
                            "offerDuration", "ONE_MONTH_TO_THREE_MONTHS"
                    )
            )));
            assertThat(result).isInstanceOf(Ok.class);
            assertThat(((ProjectJobOffer) ((Ok<JobOffer>) result).value()).settlement())
                    .isInstanceOf(PerHourSettlement.class);
        }

        @Test
        @DisplayName("lowerBoundが0の場合エラー")
        void invalidLowerBound() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "project",
                    "title", "Web Dev",
                    "description", "Build a website",
                    "offerExpireDate", "2026-12-31",
                    "settlement", Map.of(
                            "type", "fixed",
                            "numberOfWorkers", 3,
                            "budget", Map.of(
                                    "type", "range",
                                    "lowerBound", 0,
                                    "upperBound", 500000
                            )
                    )
            )));
            assertThat(result).isInstanceOf(Err.class);
        }

        @Test
        @DisplayName("titleが空の場合エラー")
        void blankTitle() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "project",
                    "title", "",
                    "description", "Build a website",
                    "offerExpireDate", "2026-12-31",
                    "settlement", Map.of(
                            "type", "fixed",
                            "numberOfWorkers", 3,
                            "budget", Map.of("type", "undecided")
                    )
            )));
            assertThat(result).isInstanceOf(Err.class);
        }
    }

    @Nested
    @DisplayName("TASK")
    class TaskTests {
        @Test
        @DisplayName("Limited: 有効な入力")
        void validLimited() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 100,
                    "numberOfTaskUnits", 1000,
                    "limitTaskUnitsPerWorker", Map.of("type", "limited", "limit", 50)
            )));
            assertThat(result).isInstanceOf(Ok.class);
            assertThat(((Ok<JobOffer>) result).value()).isInstanceOf(TaskJobOffer.class);
        }

        @Test
        @DisplayName("Unlimited: 有効な入力")
        void validUnlimited() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 100,
                    "numberOfTaskUnits", 1000,
                    "limitTaskUnitsPerWorker", Map.of("type", "unlimited")
            )));
            assertThat(result).isInstanceOf(Ok.class);
        }

        @Test
        @DisplayName("ratePerTaskUnitが小さすぎる場合エラー")
        void rateTooLow() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "task",
                    "title", "Data Entry",
                    "description", "Process survey responses",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 3,
                    "numberOfTaskUnits", 1000,
                    "limitTaskUnitsPerWorker", Map.of("type", "unlimited")
            )));
            assertThat(result).isInstanceOf(Err.class);
        }
    }

    @Nested
    @DisplayName("COMPETITION")
    class CompetitionTests {
        @Test
        @DisplayName("Standard: 有効な入力")
        void validStandard() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of("type", "standard")
            )));
            assertThat(result).isInstanceOf(Ok.class);
            CompetitionJobOffer c = (CompetitionJobOffer) ((Ok<JobOffer>) result).value();
            assertThat(c.contractPrice()).isInstanceOf(ContractPrice.Standard.class);
        }

        @Test
        @DisplayName("Custom: 有効な入力")
        void validCustom() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of("type", "custom", "value", 50000)
            )));
            assertThat(result).isInstanceOf(Ok.class);
            CompetitionJobOffer c = (CompetitionJobOffer) ((Ok<JobOffer>) result).value();
            assertThat(c.contractPrice()).isInstanceOf(ContractPrice.Custom.class);
        }

        @Test
        @DisplayName("不正なtypeの場合エラー")
        void invalidContractPriceType() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of("type", "invalid")
            )));
            assertThat(result).isInstanceOf(Err.class);
        }

        @Test
        @DisplayName("Custom valueが0の場合エラー")
        void customValueZero() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "competition",
                    "title", "Logo Design",
                    "description", "Design a company logo",
                    "offerExpireDate", "2026-12-31",
                    "contractPrice", Map.of("type", "custom", "value", 0)
            )));
            assertThat(result).isInstanceOf(Err.class);
        }
    }

    @Nested
    @DisplayName("共通")
    class CommonTests {
        @Test
        @DisplayName("不正なtypeの場合エラー")
        void invalidType() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "invalid",
                    "title", "Title",
                    "description", "Desc",
                    "offerExpireDate", "2026-12-31"
            )));
            assertThat(result).isInstanceOf(Err.class);
        }

        @Test
        @DisplayName("複数フィールドが不正の場合、エラーが蓄積される")
        void multipleErrors() {
            Result<JobOffer> result = JOB_OFFER_DECODER.decode(json(Map.of(
                    "type", "task",
                    "title", "",
                    "description", "",
                    "offerExpireDate", "2026-12-31",
                    "ratePerTaskUnit", 3,
                    "numberOfTaskUnits", 0,
                    "limitTaskUnitsPerWorker", Map.of("type", "unlimited")
            )));
            assertThat(result).isInstanceOf(Err.class);
            assertThat(((Err<JobOffer>) result).issues().asList().size()).isGreaterThanOrEqualTo(3);
        }
    }
}
