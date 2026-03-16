package com.example.yavi.reserve.adapter;

import com.example.yavi.reserve.domain.Customer;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.field;

@Component
public class CustomerMapper {
    private static final List<Field<?>> FIELDS = List.of(
            field("name"),
            field("email")
    );

    private final DSLContext dslContext;

    public CustomerMapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Customer toDomain(Record rec) {
        assert rec != null;
        return Customer.of(
                rec.get("customer_id", Long.class),
                rec.get("name", String.class),
                rec.get("email", String.class)
        );
    }

    public Record toRecord(Customer customer) {
        assert customer != null;
        Record rec;
        if (customer.getCustomerId().isDecided()) {
            rec = dslContext.newRecord(Stream.concat(
                    Stream.of(field("customer_id")), FIELDS.stream()
            ).toList());
            rec.set(field("customer_id"), customer.getCustomerId().getValue());
        } else {
            rec = dslContext.newRecord(FIELDS);
        }
        rec.set(field("name"), customer.getName());
        rec.set(field("email"), customer.getEmail());
        return rec;
    }
}
