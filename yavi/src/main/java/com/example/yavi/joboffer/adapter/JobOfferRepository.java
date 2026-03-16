package com.example.yavi.joboffer.adapter;

import com.example.yavi.joboffer.domain.JobOffer;

public interface JobOfferRepository {
    JobOffer findById(long id);
    void save(JobOffer jobOffer);
}
