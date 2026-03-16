package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Customer;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.field;

@Component
public class CustomerGatewayImpl implements CustomerGateway {
    private final DSLContext dslContext;

    public CustomerGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Result<Customer> findById(long customerId) {
        var record = dslContext.select(
                        field("customer_id"),
                        field("name"),
                        field("email"))
                .from("customers")
                .where(field("customer_id").eq(customerId))
                .fetchOne();
        if (record == null) {
            return Result.fail("not_found", "顧客が見つかりません");
        }
        return RecordDecoders.customer().decode(record);
    }
}
