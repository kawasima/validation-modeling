package com.example.raoh.joboffer.web;

import com.example.raoh.joboffer.data.JobOffer;
import com.example.raoh.joboffer.gateway.JobOfferGateway;
import net.unit8.raoh.Err;
import net.unit8.raoh.Ok;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static com.example.raoh.joboffer.web.JobOfferJsonDecoders.JOB_OFFER_DECODER;

@RestController
@RequestMapping("/api/job-offers")
public class JobOfferController {
    private final JobOfferGateway jobOfferGateway;

    public JobOfferController(JobOfferGateway jobOfferGateway) {
        this.jobOfferGateway = jobOfferGateway;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody JsonNode jsonNode) {
        return switch (JOB_OFFER_DECODER.decode(jsonNode)) {
            case Ok(JobOffer jobOffer) -> {
                String jobOfferId = jobOfferGateway.save(jobOffer);
                yield ResponseEntity.ok(Map.of(
                        "jobOfferId", jobOfferId,
                        "title", jobOffer.title(),
                        "type", jobOffer.getClass().getSimpleName()
                ));
            }
            case Err(var issues) -> ResponseEntity.badRequest().body(Map.of(
                    "errors", issues.asList().stream()
                            .map(i -> Map.of("path", i.path().toString(), "message", i.message()))
                            .toList()
            ));
        };
    }
}
