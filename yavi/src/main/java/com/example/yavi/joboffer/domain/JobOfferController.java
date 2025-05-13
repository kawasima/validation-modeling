package com.example.yavi.joboffer.domain;

import am.ik.yavi.core.Validated;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class JobOfferController {
    @PostMapping("/job-offer")
    public void register(@RequestParam Map<String, Object> params) {
        Validated<JobOffer> validated = JobOffer.mapValidator.validate(params);
        validated.map(jobOffer -> {
            // ここに業務処理が入る
            return jobOffer;
        }).mapErrors(errors -> {
            // バリデーションエラー時の扱い
            return errors;
        });
    }
}
