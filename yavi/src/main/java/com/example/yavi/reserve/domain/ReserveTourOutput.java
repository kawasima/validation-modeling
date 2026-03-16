package com.example.yavi.reserve.domain;

import com.example.yavi.enrollment.domain.Identifier;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ReserveTourOutput {
    private final Identifier reservationId;
    private final String tourCode;
    private final int adultCount;
    private final int childCount;

    public ReserveTourOutput(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.tourCode = reservation.getTour().getTourCode();
        this.adultCount = reservation.getAdultCount();
        this.childCount = reservation.getChildCount();
    }
}
