package ru.practicum.ewm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ClientConfig {
    @Bean
    public RestClient restClient(@Value("${stat-service.host}") String host,
                                 @Value("${stat-service.protocol}") String protocol,
                                 @Value("${stat-service.port}") String port) {
        return RestClient.builder()
                .baseUrl(String.format("%s://%s:%s", protocol, host, port))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper(@Value("${application.date-time-format}") String dateTimeFormat) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

        javaTimeModule.addSerializer(new LocalDateTimeSerializer(formatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}
