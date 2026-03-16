package com.example.yavi.reserve.domain;

import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validated;
import com.example.yavi.reserve.adapter.TourRepository;

public class ReservationRule {
    private final TourRepository tourRepository;

    public ReservationRule(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public Validated<CanReserveTour> apply(Tour tour, Customer customer, ReserveTourInput input) {
        int totalParticipants = input.adultCount() + input.childCount();
        int availableCapacity = tourRepository.availableCapacity(tour.getTourId());
        if (availableCapacity < totalParticipants) {
            return Validated.failureWith(ConstraintViolation.builder()
                    .name("capacity")
                    .message("ツアーに空きがありません"));
        }
        return Validated.successWith(new CanReserveTour(tour, customer, input));
    }
}
