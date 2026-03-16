package com.example.yavi.joboffer;

import am.ik.yavi.core.ConstraintViolations;
import com.example.yavi.ApiResponse;
import com.example.yavi.joboffer.adapter.JobOfferRepository;
import com.example.yavi.joboffer.domain.JobOffer;
import org.jooq.DSLContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class JobOfferController {
    private final JobOfferRepository jobOfferRepository;
    private final DSLContext dslContext;

    public JobOfferController(DSLContext dslContext, JobOfferRepository jobOfferRepository) {
        this.dslContext = dslContext;
        this.jobOfferRepository = jobOfferRepository;
    }

    @PostMapping("/job-offer")
    public ResponseEntity<ApiResponse<JobOffer>> register(@RequestBody Map<String, Object> params) {
        return JobOffer.mapValidator.validate(params)
                .fold(
                        errors -> ResponseEntity.badRequest()
                                .body(ApiResponse.failure(ConstraintViolations.of(errors))),
                        jobOffer -> dslContext.transactionResult(() -> {
                            jobOfferRepository.save(jobOffer);
                            return ResponseEntity.ok(ApiResponse.success(jobOffer));
                        })
                );
    }
}
