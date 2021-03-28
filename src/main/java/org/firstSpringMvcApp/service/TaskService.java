package org.firstSpringMvcApp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskService {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(initialDelay = 60_000, fixedRateString = "${checkSystemStatus}")
    public void checkSystemStatusEveryMinute(){
        logger.info("Check system status ...");
    }

    @Scheduled(cron = "${report}")
    public void dailyReport(){
        logger.info("Daily report ...");
    }
}
