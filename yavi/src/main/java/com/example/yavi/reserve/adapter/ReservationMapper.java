package com.example.yavi.reserve.adapter;

import com.example.yavi.reserve.domain.Reservation;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.jooq.impl.DSL.field;

@Component
public class ReservationMapper {
    private static final List<Field<?>> FIELDS = List.of(
            field("tour_id"),
            field("customer_id"),
            field("adult_count"),
            field("child_count"),
            field("remarks")
    );

    private final DSLContext dslContext;

    public ReservationMapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Record toRecord(Reservation reservation) {
        assert reservation != null;
        Record rec = dslContext.newRecord(FIELDS);
        rec.set(field("tour_id"), reservation.getTour().getTourId().getValue());
        rec.set(field("customer_id"), reservation.getCustomer().getCustomerId().getValue());
        rec.set(field("adult_count"), reservation.getAdultCount());
        rec.set(field("child_count"), reservation.getChildCount());
        rec.set(field("remarks"), reservation.getRemarks());
        return rec;
    }
}
