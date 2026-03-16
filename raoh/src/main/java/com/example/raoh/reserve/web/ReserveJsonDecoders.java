package com.example.raoh.reserve.web;

import com.example.raoh.reserve.data.Customer;
import com.example.raoh.reserve.data.Reservation;
import com.example.raoh.reserve.data.ReserveTourInput;
import com.example.raoh.reserve.data.Tour;
import com.example.raoh.reserve.gateway.CustomerGateway;
import com.example.raoh.reserve.gateway.TourGateway;
import net.unit8.raoh.json.JsonDecoder;

import static net.unit8.raoh.json.JsonDecoders.*;

public class ReserveJsonDecoders {
    public static JsonDecoder<Tour> tour(TourGateway tourGateway) {
        return string().flatMap(tourGateway::findByTourCode)::decode;
    }

    public static JsonDecoder<Customer> customer(CustomerGateway customerGateway) {
        return long_().flatMap(customerGateway::findById)::decode;
    }

    public static JsonDecoder<ReserveTourInput> reserveTourInput(TourGateway tourGateway, CustomerGateway customerGateway) {
        return combine(
                field("tourCode", tour(tourGateway)),
                field("customerId", customer(customerGateway)),
                field("adultCount", int_().min(0).max(5)),
                field("childCount", int_().min(0).max(5)),
                field("remarks", allowBlankString().maxLength(80))
        ).map(ReserveTourInput::new)::decode;
    }
}
