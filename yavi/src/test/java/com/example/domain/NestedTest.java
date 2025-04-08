package com.example.domain;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class NestedTest {
    record City(String name) {
    }

    record Country(String name) {
    }

    record Address(Country country, City city) {
    }

    @Test
    void nested() {
        Validator<Country> countryValidator = ValidatorBuilder.<Country> of()
                .constraint(Country::name, "name", c -> c.notBlank().lessThanOrEqual(20))
                .build();
        Validator<City> cityValidator = ValidatorBuilder.<City> of()
                .constraint(City::name, "name", c -> c.notBlank().lessThanOrEqual(100))
                .build();

        Validator<Address> validator = ValidatorBuilder.<Address> of()
                .nest(Address::country, "country", countryValidator)
                .nest(Address::city, "city", cityValidator)
                .build();

        ConstraintViolations violations = validator.validate(new Address(new Country("The United States of America"), new City("New York")));
        System.out.println(violations);
    }
}
