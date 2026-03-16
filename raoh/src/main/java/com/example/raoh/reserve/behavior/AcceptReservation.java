package com.example.raoh.reserve.behavior;

import com.example.raoh.reserve.data.Reservation;
import com.example.raoh.reserve.data.ReserveTourInput;
import com.example.raoh.reserve.gateway.TourGateway;
import net.unit8.raoh.Result;

import java.util.function.Function;

/**
 * behavior 予約を受け付ける = 予約入力 -> 予約
 *
 * 残席数が参加人数以上であること
 */
public class AcceptReservation implements Function<ReserveTourInput, Result<Reservation>> {
    private final TourGateway tourGateway;

    public AcceptReservation(TourGateway tourGateway) {
        this.tourGateway = tourGateway;
    }

    @Override
    public Result<Reservation> apply(ReserveTourInput input) {
        int totalParticipants = input.adultCount() + input.childCount();
        int availableCapacity = tourGateway.availableCapacity(input.tour().tourId());

        if (availableCapacity < totalParticipants) {
            return Result.fail("capacity", "ツアーに空きがありません");
        }

        return Result.ok(new Reservation(
                input.tour(),
                input.customer(),
                input.adultCount(),
                input.childCount(),
                input.remarks()
        ));
    }
}
