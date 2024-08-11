package com.kaluzny.demo.service;

import com.kaluzny.demo.domain.Automobile;
import com.kaluzny.demo.domain.AutomobileRepository;
import com.kaluzny.demo.util.AutomobileUtils;
import jakarta.jms.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomobileTopicService implements IAutomobileTopicService {

    private final AutomobileRepository repository;
    private final JmsTemplate jmsTemplate;

    public ResponseEntity<Automobile> pushMessage(Automobile automobile) {
        try {
            Topic autoTopic = Objects.requireNonNull(jmsTemplate
                            .getConnectionFactory())
                    .createConnection()
                    .createSession()
                    .createTopic("AutoTopic");
            Automobile savedAutomobile = repository.save(automobile);
            log.info("\u001B[32m" + "Sending Automobile with id: " + savedAutomobile.getId() + "\u001B[0m");
            jmsTemplate.convertAndSend(autoTopic, savedAutomobile);

            if (!AutomobileUtils.isCherryFerrary(savedAutomobile)) {
                publishCarListByColor(savedAutomobile.getColor());
            }
            return new ResponseEntity<>(savedAutomobile, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void countAndSendColor(String color) {
        long count = repository.countByColor(color);
        try {
            Topic countColorTopic = Objects.requireNonNull(jmsTemplate
                            .getConnectionFactory())
                    .createConnection()
                    .createSession()
                    .createTopic("CountColorTopic");
            Map<String, Long> colorCount = new HashMap<>();
            colorCount.put(color, count);
            log.info("\u001B[35m" + "Sending color count: " + colorCount + "\u001B[0m");
            jmsTemplate.convertAndSend(countColorTopic, colorCount);
        } catch (Exception exception) {
            log.error("Error sending color count", exception);
        }
    }

    public void publishCarListByColor(String color) {
        try {
            Topic carListByColorTopic = Objects.requireNonNull(jmsTemplate
                            .getConnectionFactory())
                    .createConnection()
                    .createSession()
                    .createTopic("CarListByColorTopic");
            List<Automobile> carList = repository.findByColor(color);
            log.info("\u001B[35m" + "Sending car list by color: " + carList + "\u001B[0m");
            jmsTemplate.convertAndSend(carListByColorTopic, carList);
        } catch (Exception exception) {
            log.error("Error sending car list by color", exception);
        }
    }
}
