package com.example.raoh.reserve.web;

import com.example.raoh.reserve.data.Customer;
import com.example.raoh.reserve.data.Tour;
import com.example.raoh.reserve.gateway.CustomerGateway;
import com.example.raoh.reserve.gateway.ReservationGateway;
import com.example.raoh.reserve.gateway.TourGateway;
import net.unit8.raoh.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReserveTourControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static JsonNode toJson(Map<String, Object> map) {
        return objectMapper.valueToTree(map);
    }

    @Test
    @DisplayName("有効なリクエストの場合、予約が成功する")
    void reserveSuccess() {
        TourGateway tourGateway = mock(TourGateway.class);
        when(tourGateway.findByTourCode("T001"))
                .thenReturn(Result.ok(new Tour(1L, "T001", "Tokyo City Tour", 20)));
        when(tourGateway.availableCapacity(1L)).thenReturn(15);

        CustomerGateway customerGateway = mock(CustomerGateway.class);
        when(customerGateway.findById(1L))
                .thenReturn(Result.ok(new Customer(1L, "Tanaka Taro", "tanaka@example.com")));

        ReservationGateway reservationGateway = mock(ReservationGateway.class);
        when(reservationGateway.save(any())).thenReturn("R001");

        ReserveTourController controller = new ReserveTourController(tourGateway, customerGateway, reservationGateway);

        JsonNode json = toJson(Map.of(
                "tourCode", "T001",
                "customerId", 1,
                "adultCount", 2,
                "childCount", 1,
                "remarks", "Window seat"
        ));

        ResponseEntity<?> response = controller.reserve(json);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("adultCountが範囲外の場合、バリデーションエラー")
    void adultCountOutOfRange() {
        TourGateway tourGateway = mock(TourGateway.class);
        when(tourGateway.findByTourCode("T001"))
                .thenReturn(Result.ok(new Tour(1L, "T001", "Tokyo City Tour", 20)));

        CustomerGateway customerGateway = mock(CustomerGateway.class);
        when(customerGateway.findById(1L))
                .thenReturn(Result.ok(new Customer(1L, "Tanaka Taro", "tanaka@example.com")));

        ReservationGateway reservationGateway = mock(ReservationGateway.class);

        ReserveTourController controller = new ReserveTourController(tourGateway, customerGateway, reservationGateway);

        JsonNode json = toJson(Map.of(
                "tourCode", "T001",
                "customerId", 1,
                "adultCount", 10,
                "childCount", 0,
                "remarks", ""
        ));

        ResponseEntity<?> response = controller.reserve(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("ツアーが見つからない場合、エラー")
    void tourNotFound() {
        TourGateway tourGateway = mock(TourGateway.class);
        when(tourGateway.findByTourCode("INVALID"))
                .thenReturn(Result.fail("not_found", "ツアーが見つかりません"));

        CustomerGateway customerGateway = mock(CustomerGateway.class);
        when(customerGateway.findById(1L))
                .thenReturn(Result.ok(new Customer(1L, "Tanaka Taro", "tanaka@example.com")));

        ReservationGateway reservationGateway = mock(ReservationGateway.class);

        ReserveTourController controller = new ReserveTourController(tourGateway, customerGateway, reservationGateway);

        JsonNode json = toJson(Map.of(
                "tourCode", "INVALID",
                "customerId", 1,
                "adultCount", 2,
                "childCount", 0,
                "remarks", ""
        ));

        ResponseEntity<?> response = controller.reserve(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("残席不足の場合、ビジネスエラー")
    void noCapacity() {
        TourGateway tourGateway = mock(TourGateway.class);
        when(tourGateway.findByTourCode("T001"))
                .thenReturn(Result.ok(new Tour(1L, "T001", "Tokyo City Tour", 20)));
        when(tourGateway.availableCapacity(1L)).thenReturn(0);

        CustomerGateway customerGateway = mock(CustomerGateway.class);
        when(customerGateway.findById(1L))
                .thenReturn(Result.ok(new Customer(1L, "Tanaka Taro", "tanaka@example.com")));

        ReservationGateway reservationGateway = mock(ReservationGateway.class);

        ReserveTourController controller = new ReserveTourController(tourGateway, customerGateway, reservationGateway);

        JsonNode json = toJson(Map.of(
                "tourCode", "T001",
                "customerId", 1,
                "adultCount", 2,
                "childCount", 1,
                "remarks", ""
        ));

        ResponseEntity<?> response = controller.reserve(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("必須フィールドが欠けている場合、バリデーションエラー")
    void missingRequiredField() {
        TourGateway tourGateway = mock(TourGateway.class);
        CustomerGateway customerGateway = mock(CustomerGateway.class);
        ReservationGateway reservationGateway = mock(ReservationGateway.class);

        ReserveTourController controller = new ReserveTourController(tourGateway, customerGateway, reservationGateway);

        JsonNode json = toJson(Map.of());  // 全フィールド欠落

        ResponseEntity<?> response = controller.reserve(json);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
