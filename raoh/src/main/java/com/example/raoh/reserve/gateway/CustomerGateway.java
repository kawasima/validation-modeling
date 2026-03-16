package com.example.raoh.reserve.gateway;

import com.example.raoh.reserve.data.Customer;
import net.unit8.raoh.Result;

public interface CustomerGateway {
    Result<Customer> findById(long customerId);
}
