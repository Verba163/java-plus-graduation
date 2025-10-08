package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.analyzer.runner.UserActionProcessor;

@SpringBootApplication
@EnableDiscoveryClient
public class AnalyzerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerApplication.class, args);
        UserActionProcessor userActionProcessor = context.getBean(UserActionProcessor.class);
        userActionProcessor.run();
    }
}