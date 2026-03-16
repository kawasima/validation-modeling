package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Tour;
import net.unit8.raoh.Result;

public interface TourGateway {
    Result<Tour> findByTourCode(String tourCode);
    int availableCapacity(long tourId);
}
