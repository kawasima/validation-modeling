package com.example.yavi.reserve;

import com.example.yavi.ApiResponse;
import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.adapter.CustomerRepository;
import com.example.yavi.reserve.adapter.ReservationRepository;
import com.example.yavi.reserve.adapter.TourRepository;
import com.example.yavi.reserve.domain.CanReserveTour;
import com.example.yavi.reserve.domain.Customer;
import com.example.yavi.reserve.domain.Tour;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class ReserveTourControllerTest {

    @Test
    @DisplayName("ツアーを予約できる")
    void testReserveTour(@Autowired DSLContext dslContext) {
        Tour tour = Tour.of(1L, "T001", "Tokyo City Tour", 20);
        Customer customer = Customer.of(1L, "Tanaka Taro", "tanaka@example.com");

        TourRepository tourRepository = mock(TourRepository.class);
        when(tourRepository.findByTourCode("T001")).thenReturn(tour);
        when(tourRepository.availableCapacity(any(Identifier.class))).thenReturn(15);

        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.findById(any(Identifier.class))).thenReturn(customer);

        ReservationRepository reservationRepository = mock(ReservationRepository.class);

        ReserveTourController controller = new ReserveTourController(dslContext, tourRepository, customerRepository, reservationRepository);

        ResponseEntity<ApiResponse<CanReserveTour>> response = controller.reserve("T001", 1L, "2", "1", "Window seat");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().isSuccess()).isTrue();
    }

    @Test
    @DisplayName("バリデーションエラー時、400を返す")
    void testValidationError(@Autowired DSLContext dslContext) {
        TourRepository tourRepository = mock(TourRepository.class);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        ReservationRepository reservationRepository = mock(ReservationRepository.class);

        ReserveTourController controller = new ReserveTourController(dslContext, tourRepository, customerRepository, reservationRepository);

        // adultCount = 10 is out of range (max 5)
        ResponseEntity<ApiResponse<CanReserveTour>> response = controller.reserve("T001", 1L, "10", "0", "");

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody().isSuccess()).isFalse();
    }

    @Test
    @DisplayName("残席不足の場合、エラーを返す")
    void testNoCapacity(@Autowired DSLContext dslContext) {
        Tour tour = Tour.of(1L, "T001", "Tokyo City Tour", 20);
        Customer customer = Customer.of(1L, "Tanaka Taro", "tanaka@example.com");

        TourRepository tourRepository = mock(TourRepository.class);
        when(tourRepository.findByTourCode("T001")).thenReturn(tour);
        when(tourRepository.availableCapacity(any(Identifier.class))).thenReturn(0);

        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.findById(any(Identifier.class))).thenReturn(customer);

        ReservationRepository reservationRepository = mock(ReservationRepository.class);

        ReserveTourController controller = new ReserveTourController(dslContext, tourRepository, customerRepository, reservationRepository);

        ResponseEntity<ApiResponse<CanReserveTour>> response = controller.reserve("T001", 1L, "2", "1", "");

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody().isSuccess()).isFalse();
    }
}
