package com.kaluzny.demo.listener;

import com.kaluzny.demo.domain.Automobile;
import com.kaluzny.demo.domain.AutomobileRepository;
import com.kaluzny.demo.service.AutomobileTopicService;
import com.kaluzny.demo.util.AutomobileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

    private final AutomobileTopicService automobileTopicService;
    private final AutomobileRepository automobileRepository;

    @JmsListener(destination = "AutoTopic", containerFactory = "automobileJmsContFactory")
    public void getAutomobileListener1(Automobile automobile) {
        log.info("\u001B[32m" + "Automobile Consumer 1: " + automobile + "\u001B[0m");
        automobileTopicService.countAndSendColor(automobile.getColor());
    }

    @JmsListener(destination = "AutoTopic", containerFactory = "automobileJmsContFactory")
    public void getAutomobileListener2(Automobile automobile) {
        log.info("\u001B[33m" + "Automobile Consumer 2: " + automobile + "\u001B[0m");
        if (AutomobileUtils.isCherryFerrary(automobile)) {
            automobile.setOriginalColor(false);
            automobileRepository.save(automobile);
            log.info("\u001B[31m" + "Updated Automobile with id: " + automobile.getId() + " to set originalColor to false" + "\u001B[0m");
            automobileTopicService.publishCarListByColor(automobile.getColor());
        }
    }

    @JmsListener(destination = "AutoTopic", containerFactory = "automobileJmsContFactory")
    public void getAutomobileListener3(Automobile automobile) {
        log.info("\u001B[34m" + "Automobile Consumer 3: " + automobile + "\u001B[0m");
    }

    @JmsListener(destination = "CountColorTopic", containerFactory = "automobileJmsContFactory")
    public void getColorCountListener(Map<String, Long> colorCount) {
        log.info("\u001B[36m" + "Received color count: " + colorCount + "\u001B[0m");
    }

    @JmsListener(destination = "CarListByColorTopic", containerFactory = "automobileJmsContFactory")
    public void getCarListByColorListener(List<Automobile> carList) {
        log.info("\u001B[36m" + "Received car list by color: " + carList + "\u001B[0m");
    }
}
