package com.example.yavi.reserve.domain;

import com.example.yavi.enrollment.domain.Identifier;

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

    public Identifier getReservationId() {
        return reservationId;
    }

    public String getTourCode() {
        return tourCode;
    }

    public int getAdultCount() {
        return adultCount;
    }

    public int getChildCount() {
        return childCount;
    }

    @Override
    public String toString() {
        return "ReserveTourOutput(reservationId=" + reservationId + ", tourCode=" + tourCode
                + ", adultCount=" + adultCount + ", childCount=" + childCount + ")";
    }
}
