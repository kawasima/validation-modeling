package com.example.yavi.joboffer;

import com.example.yavi.ApiResponse;
import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.joboffer.adapter.JobOfferRepository;
import com.example.yavi.joboffer.domain.JobOffer;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class JobOfferControllerTest {

    @Test
    @DisplayName("有効なプロジェクト案件を登録できる")
    void testValidProjectJobOffer(@Autowired DSLContext dslContext) {
        JobOfferRepository jobOfferRepository = mock(JobOfferRepository.class);
        doNothing().when(jobOfferRepository).save(any(JobOffer.class));

        JobOfferController controller = new JobOfferController(dslContext, jobOfferRepository);

        Map<String, Object> params = Map.of(
                "type", "project",
                "title", "Web Development",
                "description", "Build a website",
                "offerExpireDate", "2026-12-31",
                "settlement", Map.of(
                        "type", "fixed",
                        "numberOfWorkers", 3L,
                        "budget", Map.of(
                                "type", "range",
                                "lowerBound", 100000L,
                                "upperBound", 500000L
                        )
                )
        );

        ResponseEntity<ApiResponse<JobOffer>> response = controller.register(params);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("タイトルが空の場合、バリデーションエラーを返す")
    void testBlankTitleReturnsError(@Autowired DSLContext dslContext) {
        JobOfferRepository jobOfferRepository = mock(JobOfferRepository.class);
        JobOfferController controller = new JobOfferController(dslContext, jobOfferRepository);

        Map<String, Object> params = Map.of(
                "type", "project",
                "title", "",
                "description", "Build a website",
                "offerExpireDate", "2026-12-31",
                "settlement", Map.of(
                        "type", "fixed",
                        "numberOfWorkers", 3L,
                        "budget", Map.of(
                                "type", "range",
                                "lowerBound", 100000L,
                                "upperBound", 500000L
                        )
                )
        );

        ResponseEntity<ApiResponse<JobOffer>> response = controller.register(params);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("有効なタスク案件を登録できる")
    void testValidTaskJobOffer(@Autowired DSLContext dslContext) {
        JobOfferRepository jobOfferRepository = mock(JobOfferRepository.class);
        doNothing().when(jobOfferRepository).save(any(JobOffer.class));

        JobOfferController controller = new JobOfferController(dslContext, jobOfferRepository);

        Map<String, Object> params = Map.of(
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
        );

        ResponseEntity<ApiResponse<JobOffer>> response = controller.register(params);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("有効なコンペ案件を登録できる")
    void testValidCompetitionJobOffer(@Autowired DSLContext dslContext) {
        JobOfferRepository jobOfferRepository = mock(JobOfferRepository.class);
        doNothing().when(jobOfferRepository).save(any(JobOffer.class));

        JobOfferController controller = new JobOfferController(dslContext, jobOfferRepository);

        Map<String, Object> params = Map.of(
                "type", "competition",
                "title", "Logo Design",
                "description", "Design a company logo",
                "offerExpireDate", "2026-12-31",
                "contractPrice", Map.of(
                        "type", "standard"
                )
        );

        ResponseEntity<ApiResponse<JobOffer>> response = controller.register(params);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().isSuccess()).isTrue();
    }
}
