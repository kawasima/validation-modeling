package com.example.yavi.joboffer.adapter;

import com.example.yavi.enrollment.adapter.RecordNotFoundException;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.joboffer.domain.JobOffer;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JobOfferRepositoryImpl implements JobOfferRepository {
    private final DSLContext dslContext;
    private final JobOfferMapper jobOfferMapper;

    public JobOfferRepositoryImpl(DSLContext dslContext, JobOfferMapper jobOfferMapper) {
        this.dslContext = dslContext;
        this.jobOfferMapper = jobOfferMapper;
    }

    @Override
    public JobOffer findById(long id) {
        return Optional.ofNullable(dslContext.select()
                        .from("job_offers")
                        .where("id = ?", id)
                        .fetchOne())
                .map(jobOfferMapper::toDomain)
                .orElseThrow(() -> new RecordNotFoundException(Identifier.of(id)));
    }

    @Override
    public void save(JobOffer jobOffer) {
        Record rec = jobOfferMapper.toRecord(jobOffer);
        dslContext.insertInto(table("job_offers"))
                .set(rec)
                .execute();
    }
}
