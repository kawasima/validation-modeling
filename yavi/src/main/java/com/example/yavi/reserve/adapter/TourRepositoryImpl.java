package com.example.yavi.reserve.adapter;

import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.domain.Tour;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class TourRepositoryImpl implements TourRepository {
    private final DSLContext dslContext;
    private final TourMapper tourMapper;

    public TourRepositoryImpl(DSLContext dslContext, TourMapper tourMapper) {
        this.dslContext = dslContext;
        this.tourMapper = tourMapper;
    }

    @Override
    public Tour findByTourCode(String tourCode) {
        return Optional.ofNullable(dslContext.select()
                        .from("tours")
                        .where("tour_code = ?", tourCode)
                        .fetchOne())
                .map(tourMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Tour not found: " + tourCode));
    }

    @Override
    public int availableCapacity(Identifier tourId) {
        Tour tour = Optional.ofNullable(dslContext.select()
                        .from("tours")
                        .where("tour_id = ?", tourId.getValue())
                        .fetchOne())
                .map(tourMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Tour not found: " + tourId));

        Integer reservedCount = dslContext.selectCount()
                .from("reservations")
                .where("tour_id = ?", tourId.getValue())
                .fetchOne(0, int.class);

        return tour.getCapacity() - Objects.requireNonNullElse(reservedCount, 0);
    }
}
