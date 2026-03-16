package com.example.jsr380.joboffer;

import org.springframework.stereotype.Service;

@Service
public class JobOfferServiceImpl implements JobOfferService {

    @Override
    public JobOfferOutput register(JobOfferInput input) {
        // 本来はDBに保存
        String jobOfferId = "JO" + System.currentTimeMillis();
        return new JobOfferOutput(jobOfferId, input.getTitle(), input.getJobOfferType());
    }
}
