package com.example.jsr380.joboffer;

import lombok.Data;

@Data
public class JobOfferOutput {
    private String jobOfferId;
    private String title;
    private String jobOfferType;

    public JobOfferOutput(String jobOfferId, String title, String jobOfferType) {
        this.jobOfferId = jobOfferId;
        this.title = title;
        this.jobOfferType = jobOfferType;
    }
}
