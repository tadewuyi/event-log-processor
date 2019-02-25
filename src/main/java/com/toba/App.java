package com.toba;

import com.toba.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        EventLogProcessor eventLogProcessor = context.getBean(EventLogProcessor.class);
        try {
            logger.info("Starting Application...");
            eventLogProcessor.run();
        } catch (Exception e) {
            logger.error(e);
        } finally {
            context.close();
        }
    }
}
