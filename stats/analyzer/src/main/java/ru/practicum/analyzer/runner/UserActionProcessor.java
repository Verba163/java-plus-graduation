package ru.practicum.analyzer.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.consumer.KafkaConsumerService;
import ru.practicum.analyzer.handlers.UserActionHandler;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor implements Runnable {

    private final KafkaConsumerService consumer;
    private final UserActionHandler userActionHandler;

    @Value("${kafka.action-topic}")
    private String topic;

    private volatile boolean running = true;

    @Override
    public void run() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered, waking up Kafka consumer...");
            running = false;
            consumer.wakeup();
        }));

        try {
            log.info("Subscribing to topic {}", topic);
            consumer.subscribe(List.of(topic));

            while (running) {
                log.info("Waiting for messages...");
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(5000));
                log.info("Received {} message(s)", records.count());

                if (!records.isEmpty()) {
                    for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                        UserActionAvro avro = (UserActionAvro) record.value();
                        log.info("Processing user action: {}", avro);
                        try {
                            userActionHandler.handle(avro);
                            log.info("User action processed successfully: {}", avro);
                        } catch (Exception e) {
                            log.error("Error processing user action: {}", avro, e);
                        }
                    }
                    log.info("Committing offsets asynchronously");
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException e) {
            if (running) {

                log.warn("WakeupException during polling", e);
            } else {
                log.info("Kafka consumer wakeup due to shutdown");
            }
        } catch (Exception e) {
            log.error("Error while processing messages", e);
        } finally {
            closeConsumer();
        }
    }

    private void closeConsumer() {
        try {
            log.info("Committing offsets before closing consumer");
            consumer.commitAsync();
        } catch (Exception e) {
            log.error("Error committing offsets during shutdown", e);
        } finally {
            try {
                log.info("Closing Kafka consumer");
                consumer.close();
            } catch (Exception e) {
                log.error("Error closing Kafka consumer", e);
            }
        }
    }
}