package com.cgi.camunda.poc.model;

import lombok.Data;

@Data
public class Account {
    private String ssn;
    private String accountNumber;
    private String paymentAmount;
}
