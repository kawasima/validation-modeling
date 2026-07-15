package com.example.raoh.reserve.web;

import com.example.raoh.reserve.data.Reservation;
import net.unit8.raoh.encode.Encoder;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static net.unit8.raoh.encode.MapEncoders.*;
import static net.unit8.raoh.encode.ObjectEncoders.*;

/**
 * ツアー予約レスポンスのエンコーダ。{@link ReserveJsonDecoders} の write 側の対。
 */
public final class ReserveEncoders {

    private ReserveEncoders() {}

    /**
     * data 予約受付済 = 予約ID AND 予約
     */
    public record ReservationAccepted(@Nullable String reservationId, Reservation reservation) {}

    /**
     * 受け付けた予約のレスポンス。予約IDは gateway が採番するので予約そのものには含まれない。
     */
    public static final Encoder<ReservationAccepted, Map<String, @Nullable Object>> RESERVATION_ACCEPTED = object(
            nullableProperty("reservationId", ReservationAccepted::reservationId, string()),
            property("tourCode",   (ReservationAccepted a) -> a.reservation().tour().tourCode(), string()),
            property("adultCount", (ReservationAccepted a) -> a.reservation().adultCount(),      int_()),
            property("childCount", (ReservationAccepted a) -> a.reservation().childCount(),      int_()));
}
