package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Reservation;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
public class ReservationGatewayImpl implements ReservationGateway {
    private final DSLContext dslContext;

    public ReservationGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public String save(Reservation reservation) {
        Record rec = dslContext.insertInto(table("reservations"))
                .set(dslContext.newRecord(
                        field("tour_id"),
                        field("customer_id"),
                        field("adult_count"),
                        field("child_count"),
                        field("remarks")
                ))
                .set(field("tour_id"), reservation.tour().tourId())
                .set(field("customer_id"), reservation.customer().customerId())
                .set(field("adult_count"), reservation.adultCount())
                .set(field("child_count"), reservation.childCount())
                .set(field("remarks"), reservation.remarks())
                .returning(field("reservation_id"))
                .fetchOne();
        return rec != null ? rec.get("reservation_id", String.class) : null;
    }
}
