package com.example.yavi.reserve.domain;

import am.ik.yavi.arguments.Arguments4Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.IntegerValidator;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.IntegerValidatorBuilder;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.ViolationMessage;
import com.example.yavi.enrollment.domain.Identifier;

public class Tour {
    private final Identifier tourId;
    private final String tourCode;
    private final String name;
    private final int capacity;

    private Tour(Identifier tourId, String tourCode, String name, int capacity) {
        this.tourId = tourId;
        this.tourCode = tourCode;
        this.name = name;
        this.capacity = capacity;
    }

    public Identifier getTourId() {
        return tourId;
    }

    public String getTourCode() {
        return tourCode;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "Tour(tourId=" + tourId + ", tourCode=" + tourCode + ", name=" + name + ", capacity=" + capacity + ")";
    }

    public static final LongValidator<Identifier> tourIdValidator = LongValidatorBuilder.of("tourId",
                    c -> c.predicate(id -> id > 0L || id == -1L, ViolationMessage.of("identifier", "must be greater than 0 or -1"))
            ).build()
            .andThen(id -> id < 0L ? Identifier.undecided() : Identifier.of(id));

    static final StringValidator<String> tourCodeValidator = StringValidatorBuilder.of("tourCode",
            c -> c.notBlank().lessThanOrEqual(10)
    ).build();

    static final StringValidator<String> nameValidator = StringValidatorBuilder.of("name",
            c -> c.notBlank().lessThan(100)
    ).build();

    static final IntegerValidator<Integer> capacityValidator = IntegerValidatorBuilder.of("capacity",
            c -> c.greaterThan(0)
    ).build();

    static final Arguments4Validator<Long, String, String, Integer, Tour> validator = ArgumentsValidators.split(
            tourIdValidator,
            tourCodeValidator,
            nameValidator,
            capacityValidator
    ).apply(Tour::new);

    public static Tour of(long tourId, String tourCode, String name, int capacity) {
        return validator.validated(tourId, tourCode, name, capacity);
    }

    public static Validated<Tour> parse(long tourId, String tourCode, String name, int capacity) {
        return validator.validate(tourId, tourCode, name, capacity);
    }
}
