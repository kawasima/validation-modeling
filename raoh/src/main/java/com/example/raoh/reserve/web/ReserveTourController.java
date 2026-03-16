package com.example.raoh.reserve.web;

import com.example.raoh.reserve.behavior.AcceptReservation;
import com.example.raoh.reserve.data.ReserveTourInput;
import com.example.raoh.reserve.data.Reservation;
import com.example.raoh.reserve.gateway.CustomerGateway;
import com.example.raoh.reserve.gateway.ReservationGateway;
import com.example.raoh.reserve.gateway.TourGateway;
import net.unit8.raoh.Err;
import net.unit8.raoh.Ok;
import net.unit8.raoh.json.JsonDecoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static com.example.raoh.reserve.web.ReserveJsonDecoders.*;

@RestController
@RequestMapping("/api/tours")
public class ReserveTourController {
    private final ReservationGateway reservationGateway;
    private final AcceptReservation acceptReservation;
    private final JsonDecoder<ReserveTourInput> reserveRequestDecoder;

    public ReserveTourController(TourGateway tourGateway,
                                 CustomerGateway customerGateway,
                                 ReservationGateway reservationGateway) {
        this.reservationGateway = reservationGateway;
        this.acceptReservation = new AcceptReservation(tourGateway);
        reserveRequestDecoder = reserveTourInput(tourGateway, customerGateway);
    }

    /**
     * ツアー予約エンドポイント
     */
    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody JsonNode jsonNode) {
        return switch (reserveRequestDecoder.decode(jsonNode)) {
            case Ok(ReserveTourInput input) -> switch (acceptReservation.apply(input)) {
                case Ok(Reservation reservation) -> {
                    String reservationId = reservationGateway.save(reservation);
                    yield ResponseEntity.ok(Map.of(
                            "reservationId", reservationId,
                            "tourCode", reservation.tour().tourCode(),
                            "adultCount", reservation.adultCount(),
                            "childCount", reservation.childCount()
                    ));
                }
                case Err(var issues) -> ResponseEntity.badRequest().body(Map.of(
                        "errors", issues.asList().stream()
                                .map(i -> Map.of("path", i.path().toString(), "message", i.message()))
                                .toList()
                ));
            };
            case Err(var issues) -> ResponseEntity.badRequest().body(Map.of(
                    "errors", issues.asList().stream()
                            .map(i -> Map.of("path", i.path().toString(), "message", i.message()))
                            .toList()
            ));
        };
    }
}
