package com.example.yavi.reserve.adapter;

import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.domain.Tour;

public interface TourRepository {
    Tour findByTourCode(String tourCode);
    int availableCapacity(Identifier tourId);
}
