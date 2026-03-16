package com.example.yavi.reserve.adapter;

import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.reserve.domain.Customer;

public interface CustomerRepository {
    Customer findById(Identifier customerId);
    void save(Customer customer);
}
