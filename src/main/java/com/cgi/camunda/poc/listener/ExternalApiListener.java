package com.cgi.camunda.poc.listener;

import com.cgi.camunda.poc.model.Account;
import com.cgi.camunda.poc.model.PaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class ExternalApiListener {

    @Autowired
    RuntimeService runtimeService;

    @KafkaListener(topics = "ExternalPaymentTopic", groupId = "group_id")
    public void listenExternalApi(String accountJsonObject) {

        log.info("Message Received ==> {}", accountJsonObject);
        ObjectMapper objectMapper = new ObjectMapper();
        Account account = null;
        try {
            account = objectMapper.readValue(accountJsonObject, Account.class);
        } catch (JsonProcessingException e) {
            log.error("Error when parsing Account String Object");
        }

        if(account == null) {
            return;
        }
        log.info("Received Object ==> " + account.toString());

        // Start BPM Process
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentInfo(account);
        HttpEntity<?> entity = new HttpEntity<>(paymentRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8081/poc/startProcess", HttpMethod.POST, entity, String.class);
        String result = response.getBody();
        String httpStatus = response.getStatusCode().toString();
        log.info("Result ===> {}, HttpStatus ===> ", result, httpStatus);
    }

}
