package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Tour;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TourGatewayImpl implements TourGateway {
    private final DSLContext dslContext;

    public TourGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Result<Tour> findByTourCode(String tourCode) {
        Record rec = dslContext.select()
                .from("tours")
                .where("tour_code = ?", tourCode)
                .fetchOne();
        if (rec == null) {
            return Result.fail("not_found", "ツアーが見つかりません");
        }
        return Result.ok(new Tour(
                rec.get("tour_id", Long.class),
                rec.get("tour_code", String.class),
                rec.get("name", String.class),
                rec.get("capacity", Integer.class)
        ));
    }

    @Override
    public int availableCapacity(long tourId) {
        Integer reservedCount = dslContext.selectCount()
                .from("reservations")
                .where("tour_id = ?", tourId)
                .fetchOne(0, int.class);

        Integer capacity = dslContext.select()
                .from("tours")
                .where("tour_id = ?", tourId)
                .fetchOne("capacity", Integer.class);

        return Objects.requireNonNullElse(capacity, 0) - Objects.requireNonNullElse(reservedCount, 0);
    }
}
