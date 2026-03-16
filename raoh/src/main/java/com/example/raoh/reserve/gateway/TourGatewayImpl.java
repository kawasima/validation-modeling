package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Tour;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.jooq.impl.DSL.field;

@Component
public class TourGatewayImpl implements TourGateway {
    private final DSLContext dslContext;

    public TourGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Result<Tour> findByTourCode(String tourCode) {
        var record = dslContext.select(
                        field("tour_id"),
                        field("tour_code"),
                        field("name"),
                        field("capacity"))
                .from("tours")
                .where(field("tour_code").eq(tourCode))
                .fetchOne();
        if (record == null) {
            return Result.fail("not_found", "ツアーが見つかりません");
        }
        return RecordDecoders.tour().decode(record);
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
