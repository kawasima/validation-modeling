package com.example.yavi.joboffer.adapter;

import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.joboffer.domain.*;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class JobOfferRepositoryImplTest {

    private JobOfferRepositoryImpl createRepository(DSLContext dslContext) {
        return new JobOfferRepositoryImpl(dslContext, new JobOfferMapper(dslContext));
    }

    @Test
    @DisplayName("IDでPROJECT案件を取得できる")
    void findProjectById(@Autowired DSLContext dslContext) {
        var repository = createRepository(dslContext);

        JobOffer jobOffer = repository.findById(1);
        assertThat(jobOffer.title()).isEqualTo("Web Development Project");
        assertThat(jobOffer.jobOfferType()).isEqualTo(JobOfferType.PROJECT);
        assertThat(jobOffer).isInstanceOf(ProjectJobOffer.class);
        ProjectJobOffer project = (ProjectJobOffer) jobOffer;
        assertThat(project.settlement()).isInstanceOf(FixedSettlement.class);
    }

    @Test
    @DisplayName("IDでTASK案件を取得できる")
    void findTaskById(@Autowired DSLContext dslContext) {
        var repository = createRepository(dslContext);

        JobOffer jobOffer = repository.findById(2);
        assertThat(jobOffer.title()).isEqualTo("Data Entry Task");
        assertThat(jobOffer.jobOfferType()).isEqualTo(JobOfferType.TASK);
        assertThat(jobOffer).isInstanceOf(TaskJobOffer.class);
        TaskJobOffer task = (TaskJobOffer) jobOffer;
        assertThat(task.ratePerTaskUnit()).isEqualTo(100);
        assertThat(task.numberOfTaskUnits()).isEqualTo(1000);
        assertThat(task.limitTaskUnitsPerWorker().getLimit()).hasValue(50L);
    }

    @Test
    @DisplayName("IDでCOMPETITION案件を取得できる")
    void findCompetitionById(@Autowired DSLContext dslContext) {
        var repository = createRepository(dslContext);

        JobOffer jobOffer = repository.findById(3);
        assertThat(jobOffer.title()).isEqualTo("Logo Design Competition");
        assertThat(jobOffer.jobOfferType()).isEqualTo(JobOfferType.COMPETITION);
        assertThat(jobOffer).isInstanceOf(CompetitionJobOffer.class);
        CompetitionJobOffer competition = (CompetitionJobOffer) jobOffer;
        assertThat(competition.contractPrice()).isInstanceOf(ContractPrice.Standard.class);
    }

    @Test
    @DisplayName("PROJECT案件を保存・復元できる")
    void saveAndFindProject(@Autowired DSLContext dslContext) {
        var repository = createRepository(dslContext);

        JobOffer jobOffer = JobOffer.mapValidator.validated(Map.of(
                "type", "project",
                "title", "New Project",
                "description", "A new project",
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
        ));

        repository.save(jobOffer);

        long count = dslContext.selectCount()
                .from("job_offers")
                .where("title = ?", "New Project")
                .fetchOne(0, long.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("TASK案件を保存・復元できる")
    void saveAndFindTask(@Autowired DSLContext dslContext) {
        var repository = createRepository(dslContext);

        JobOffer jobOffer = JobOffer.mapValidator.validated(Map.of(
                "type", "task",
                "title", "New Task",
                "description", "A new task",
                "offerExpireDate", "2026-12-31",
                "ratePerTaskUnit", 200L,
                "numberOfTaskUnits", 500L,
                "limitTaskUnitsPerWorker", Map.of(
                        "type", "unlimited"
                )
        ));

        repository.save(jobOffer);

        long count = dslContext.selectCount()
                .from("job_offers")
                .where("title = ?", "New Task")
                .fetchOne(0, long.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("COMPETITION案件を保存・復元できる")
    void saveAndFindCompetition(@Autowired DSLContext dslContext) {
        var repository = createRepository(dslContext);

        JobOffer jobOffer = JobOffer.mapValidator.validated(Map.of(
                "type", "competition",
                "title", "New Competition",
                "description", "A new competition",
                "offerExpireDate", "2026-12-31",
                "contractPrice", Map.of(
                        "type", "custom",
                        "value", 30000L
                )
        ));

        repository.save(jobOffer);

        long count = dslContext.selectCount()
                .from("job_offers")
                .where("title = ?", "New Competition")
                .fetchOne(0, long.class);
        assertThat(count).isEqualTo(1);
    }
}
