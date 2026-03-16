package com.example.yavi.reserve.adapter;

import com.example.yavi.reserve.domain.Reservation;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final DSLContext dslContext;
    private final ReservationMapper reservationMapper;

    public ReservationRepositoryImpl(DSLContext dslContext, ReservationMapper reservationMapper) {
        this.dslContext = dslContext;
        this.reservationMapper = reservationMapper;
    }

    @Override
    public void save(Reservation reservation) {
        Record rec = reservationMapper.toRecord(reservation);
        Record result = dslContext.insertInto(table("reservations"))
                .set(rec)
                .returning(field("reservation_id"))
                .fetchOne();
        Optional.ofNullable(result)
                .map(r -> r.get("reservation_id", Long.class))
                .ifPresent(id -> reservation.getReservationId().decide(id));
    }
}
