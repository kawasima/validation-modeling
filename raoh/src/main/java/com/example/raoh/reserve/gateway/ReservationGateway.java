package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Reservation;

public interface ReservationGateway {
    String save(Reservation reservation);
}
