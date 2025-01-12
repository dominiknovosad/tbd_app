package com.example.tbd.customer;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Povoľte Spring na správu mappera
public interface CustomerMapper {
    CustomerDTO toCustomerDTO(Customer customer);

    Customer toCustomer(CustomerDTO customerDTO);
}