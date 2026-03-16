package com.example.yavi.reserve;

import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validated;
import com.example.yavi.ApiResponse;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.adapter.CustomerRepository;
import com.example.yavi.reserve.adapter.ReservationRepository;
import com.example.yavi.reserve.adapter.TourRepository;
import com.example.yavi.reserve.domain.*;
import org.jooq.DSLContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("tours")
public class ReserveTourController {
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final DSLContext dslContext;

    public ReserveTourController(DSLContext dslContext, TourRepository tourRepository,
                                 CustomerRepository customerRepository, ReservationRepository reservationRepository) {
        this.dslContext = dslContext;
        this.tourRepository = tourRepository;
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
    }

    @PostMapping("{tourCode}/reserve")
    public ResponseEntity<ApiResponse<CanReserveTour>> reserve(
            @PathVariable("tourCode") String tourCode,
            @RequestParam("customerId") long customerId,
            @RequestParam("adultCount") String adultCount,
            @RequestParam("childCount") String childCount,
            @RequestParam(value = "remarks", defaultValue = "") String remarks) {

        // バリデーション
        Validated<Identifier> validatedCustomerId = Customer.customerIdValidator.validate(customerId);
        Validated<ReserveTourInput> validatedInput = ReserveTourInput.parse(tourCode, adultCount, childCount, remarks);

        if (!validatedCustomerId.isValid() || !validatedInput.isValid()) {
            ConstraintViolations violations = new ConstraintViolations();
            if (!validatedCustomerId.isValid()) violations.addAll(validatedCustomerId.errors());
            if (!validatedInput.isValid()) violations.addAll(validatedInput.errors());
            return ResponseEntity.badRequest().body(ApiResponse.failure(violations));
        }

        // I/O
        Tour tour = tourRepository.findByTourCode(tourCode);
        Customer customer = customerRepository.findById(validatedCustomerId.value());
        ReserveTourInput input = validatedInput.value();

        // ビジネスルール
        ReservationRule reservationRule = new ReservationRule(tourRepository);
        Validated<CanReserveTour> result = reservationRule.apply(tour, customer, input);

        return result.fold(
                errors -> ResponseEntity.badRequest()
                        .body(ApiResponse.failure(ConstraintViolations.of(errors))),
                canReserve -> dslContext.transactionResult(() -> {
                    Reservation reservation = Reservation.create(canReserve.tour(), canReserve.customer(), canReserve.input());
                    reservationRepository.save(reservation);
                    return ResponseEntity.ok(ApiResponse.success(canReserve));
                })
        );
    }
}
