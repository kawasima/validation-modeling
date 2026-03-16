package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Customer;
import com.example.raoh.reserve.data.Tour;
import net.unit8.raoh.jooq.JooqRecordDecoder;

import static net.unit8.raoh.ObjectDecoders.*;
import static net.unit8.raoh.jooq.JooqRecordDecoders.*;

public class RecordDecoders {
    private static final JooqRecordDecoder<Tour> TOUR_DECODER = combine(
            field("tour_id", long_()),
            field("tour_code", string()),
            field("name", string()),
            field("capacity", int_())
    ).map(Tour::new)::decode;

    public static JooqRecordDecoder<Tour> tour() {
        return TOUR_DECODER;
    }

    private static final JooqRecordDecoder<Customer> CUSTOMER_DECODER = combine(
            field("customer_id", long_()),
            field("name", string()),
            field("email", string())
    ).map(Customer::new)::decode;

    public static JooqRecordDecoder<Customer> customer() {
        return CUSTOMER_DECODER;
    }
}
