package com.example.jsr380.reserve;

import lombok.Data;

@Data
public class ReserveTourInput {
    private String tourCode;
    private Integer adultCount;
    private Integer childCount;
    private String remarks;
    private String customerId;
}
