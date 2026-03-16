package com.example.yavi.reserve.domain;

import com.example.yavi.enrollment.domain.Identifier;

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

    public Identifier getReservationId() {
        return reservationId;
    }

    public Tour getTour() {
        return tour;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getAdultCount() {
        return adultCount;
    }

    public int getChildCount() {
        return childCount;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return "Reservation(reservationId=" + reservationId + ", tour=" + tour + ", customer=" + customer
                + ", adultCount=" + adultCount + ", childCount=" + childCount + ", remarks=" + remarks + ")";
    }

    public static Reservation of(long reservationId, Tour tour, Customer customer, int adultCount, int childCount, String remarks) {
        return new Reservation(Identifier.of(reservationId), tour, customer, adultCount, childCount, remarks);
    }

    public static Reservation create(Tour tour, Customer customer, ReserveTourInput input) {
        return new Reservation(Identifier.undecided(), tour, customer, input.adultCount(), input.childCount(), input.remarks());
    }
}
