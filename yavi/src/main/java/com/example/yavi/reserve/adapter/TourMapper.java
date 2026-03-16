package com.example.yavi.reserve.adapter;

import com.example.yavi.reserve.domain.Tour;
import org.jooq.Record;
import org.springframework.stereotype.Component;

@Component
public class TourMapper {
    public Tour toDomain(Record rec) {
        assert rec != null;
        return Tour.of(
                rec.get("tour_id", Long.class),
                rec.get("tour_code", String.class),
                rec.get("name", String.class),
                rec.get("capacity", Integer.class)
        );
    }
}
