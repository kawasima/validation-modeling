package com.example.joboffer.domain;

import am.ik.yavi.core.Validated;
import com.example.joboffer.domain.JobOffer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JobOfferTest {
    @Test
    void projectJobOffer() {
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
                "offerExpireDate", "2025-10-10"
        ));
        assertThat(validated.isValid()).isTrue();
        System.out.println(validated.value());
    }
    @Test
    void projectJobOfferError() {
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
                "offerExpireDate", "2025-10-10"
        ));
        assertThat(validated.isValid()).isFalse();
        System.out.println(validated.errors());
    }

}