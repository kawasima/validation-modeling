package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Customer;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

@Component
public class CustomerGatewayImpl implements CustomerGateway {
    private final DSLContext dslContext;

    public CustomerGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Result<Customer> findById(long customerId) {
        Record rec = dslContext.select()
                .from("customers")
                .where("customer_id = ?", customerId)
                .fetchOne();
        if (rec == null) {
            return Result.fail("not_found", "顧客が見つかりません");
        }
        return Result.ok(new Customer(
                rec.get("customer_id", Long.class),
                rec.get("name", String.class),
                rec.get("email", String.class)
        ));
    }
}
