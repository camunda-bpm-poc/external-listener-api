package com.cgi.camunda.poc.listener;

import com.cgi.camunda.poc.model.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExternalApiListener {

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

    }

}
