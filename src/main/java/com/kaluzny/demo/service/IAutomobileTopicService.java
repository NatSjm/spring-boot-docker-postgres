package com.kaluzny.demo.service;

import com.kaluzny.demo.domain.Automobile;
import org.springframework.http.ResponseEntity;

public interface IAutomobileTopicService  {
    ResponseEntity<Automobile> pushMessage(Automobile automobile);
    void countAndSendColor(String color);
}