package com.bs23.accounts.service.impl;

import com.bs23.accounts.dto.AccountsDto;
import com.bs23.accounts.dto.CardsDto;
import com.bs23.accounts.dto.CustomerDetailsDto;
import com.bs23.accounts.dto.LoansDto;
import com.bs23.accounts.entity.Accounts;
import com.bs23.accounts.entity.Customer;
import com.bs23.accounts.exception.ResourceNotFoundException;
import com.bs23.accounts.mapper.AccountsMapper;
import com.bs23.accounts.mapper.CustomerMapper;
import com.bs23.accounts.repository.AccountsRepository;
import com.bs23.accounts.repository.CustomerRepository;
import com.bs23.accounts.service.ICustomersService;
import com.bs23.accounts.service.client.CardsFeignClient;
import com.bs23.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (Objects.nonNull(loansDtoResponseEntity)) {
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (Objects.nonNull(cardsDtoResponseEntity)) {
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }

        return customerDetailsDto;

    }
}
