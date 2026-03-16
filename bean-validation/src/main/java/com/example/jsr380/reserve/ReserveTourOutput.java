package com.example.jsr380.reserve;

import lombok.Data;

@Data
public class ReserveTourOutput {
    private String reservationId;
    private String tourCode;
    private int adultCount;
    private int childCount;

    public ReserveTourOutput(String reservationId, String tourCode, int adultCount, int childCount) {
        this.reservationId = reservationId;
        this.tourCode = tourCode;
        this.adultCount = adultCount;
        this.childCount = childCount;
    }
}
