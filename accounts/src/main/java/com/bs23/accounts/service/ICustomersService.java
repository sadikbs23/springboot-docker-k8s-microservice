package com.bs23.accounts.service;

import com.bs23.accounts.dto.CustomerDetailsDto;

public interface ICustomersService {

    CustomerDetailsDto fetchCustomerDetails(String mobileNumber,String correlationId);
}
