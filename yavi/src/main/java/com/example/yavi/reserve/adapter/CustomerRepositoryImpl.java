package com.example.yavi.reserve.adapter;

import com.example.yavi.enrollment.adapter.RecordNotFoundException;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.domain.Customer;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    private final DSLContext dslContext;
    private final CustomerMapper customerMapper;

    public CustomerRepositoryImpl(DSLContext dslContext, CustomerMapper customerMapper) {
        this.dslContext = dslContext;
        this.customerMapper = customerMapper;
    }

    @Override
    public Customer findById(Identifier customerId) {
        return Optional.ofNullable(dslContext.select()
                        .from("customers")
                        .where("customer_id = ?", customerId.getValue())
                        .fetchOne())
                .map(customerMapper::toDomain)
                .orElseThrow(() -> new RecordNotFoundException(customerId));
    }

    @Override
    public void save(Customer customer) {
        var record = customerMapper.toRecord(customer);
        if (customer.getCustomerId().isDecided()) {
            dslContext.update(table("customers"))
                    .set(record)
                    .where("customer_id = ?", customer.getCustomerId().getValue())
                    .execute();
        } else {
            Record rec = dslContext.insertInto(table("customers"))
                    .set(record)
                    .returning(field("customer_id"))
                    .fetchOne();
            Optional.ofNullable(rec)
                    .map(r -> r.get("customer_id", Long.class))
                    .ifPresent(id -> customer.getCustomerId().decide(id));
        }
    }
}
