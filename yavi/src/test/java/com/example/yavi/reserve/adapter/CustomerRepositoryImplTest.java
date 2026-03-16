package com.example.yavi.reserve.adapter;

import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.domain.Customer;
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
class CustomerRepositoryImplTest {

    @Test
    @DisplayName("IDで顧客を取得できる")
    void findById(@Autowired DSLContext dslContext) {
        CustomerMapper mapper = new CustomerMapper(dslContext);
        CustomerRepositoryImpl repository = new CustomerRepositoryImpl(dslContext, mapper);

        Customer customer = repository.findById(Identifier.of(1));
        assertThat(customer.getName()).isEqualTo("Tanaka Taro");
        assertThat(customer.getEmail()).isEqualTo("tanaka@example.com");
    }

    @Test
    @DisplayName("顧客を保存できる")
    void save(@Autowired DSLContext dslContext) {
        CustomerMapper mapper = new CustomerMapper(dslContext);
        CustomerRepositoryImpl repository = new CustomerRepositoryImpl(dslContext, mapper);

        Customer customer = Customer.create("New Customer", "new@example.com")
                .value();
        repository.save(customer);

        assertThat(customer.getCustomerId().isDecided()).isTrue();
    }
}
