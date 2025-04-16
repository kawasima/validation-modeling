package com.example.joboffer.domain;

import am.ik.yavi.core.Validated;
import com.example.joboffer.domain.FixedSettlement;
import com.example.joboffer.domain.JobOfferType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FixedSettlementTest {
    @Test
    void budgetType() {
        Validated<FixedSettlement> validated = FixedSettlement.mapValidator.validate(Map.of(
                "type", JobOfferType.PROJECT.name(),
                "budget", Map.of(
                        "type", "range",
                        "lowerBound", 1000,
                        "upperBound", 2000
                ),
                "numberOfWorkers", 5L
        ));
        assertThat(validated.isValid()).isTrue();
    }

    @Test
    void minusLowerBound() {
        Validated<FixedSettlement> validated = FixedSettlement.mapValidator.validate(Map.of(
                "type", JobOfferType.PROJECT.name(),
                "budget", Map.of(
                        "type", "range",
                        "lowerBound", -1000,
                        "upperBound", 2000
                ),
                "numberOfWorkers", 5L
        ));
        assertThat(validated.isValid()).isFalse();
    }
}