package com.example.yavi.reserve.domain;

import com.example.yavi.enrollment.domain.Identifier;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Reservation {
    private final Identifier reservationId;
    private final Tour tour;
    private final Customer customer;
    private final int adultCount;
    private final int childCount;
    private final String remarks;

    private Reservation(Identifier reservationId, Tour tour, Customer customer, int adultCount, int childCount, String remarks) {
        this.reservationId = reservationId;
        this.tour = tour;
        this.customer = customer;
        this.adultCount = adultCount;
        this.childCount = childCount;
        this.remarks = remarks;
    }

    public static Reservation of(long reservationId, Tour tour, Customer customer, int adultCount, int childCount, String remarks) {
        return new Reservation(Identifier.of(reservationId), tour, customer, adultCount, childCount, remarks);
    }

    public static Reservation create(Tour tour, Customer customer, ReserveTourInput input) {
        return new Reservation(Identifier.undecided(), tour, customer, input.adultCount(), input.childCount(), input.remarks());
    }
}
