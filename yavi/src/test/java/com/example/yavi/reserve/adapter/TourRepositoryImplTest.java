package com.example.yavi.reserve.adapter;

import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.domain.Tour;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class TourRepositoryImplTest {

    @Test
    @DisplayName("ツアーコードでツアーを取得できる")
    void findByTourCode(@Autowired DSLContext dslContext) {
        TourMapper mapper = new TourMapper();
        TourRepositoryImpl repository = new TourRepositoryImpl(dslContext, mapper);

        Tour tour = repository.findByTourCode("T001");
        assertThat(tour.getName()).isEqualTo("Tokyo City Tour");
        assertThat(tour.getCapacity()).isEqualTo(20);
    }

    @Test
    @DisplayName("残席数を取得できる")
    void availableCapacity(@Autowired DSLContext dslContext) {
        TourMapper mapper = new TourMapper();
        TourRepositoryImpl repository = new TourRepositoryImpl(dslContext, mapper);

        Tour tour = repository.findByTourCode("T001");
        int capacity = repository.availableCapacity(tour.getTourId());
        assertThat(capacity).isEqualTo(20); // No reservations yet
    }
}
