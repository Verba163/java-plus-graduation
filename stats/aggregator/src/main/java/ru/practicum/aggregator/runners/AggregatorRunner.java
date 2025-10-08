package ru.practicum.aggregator.runners;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.consumer.KafkaConsumerFactory;
import ru.practicum.aggregator.producer.KafkaProducerService;
import ru.practicum.aggregator.service.AggregationService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatorRunner {

    private final KafkaConsumerFactory kafkaConsumerFactory;
    private final KafkaProducerService producer;
    private final AggregationService aggregationService;

    @Value("${kafka.action-topic}")
    private String actionTopic;

    @Value("${kafka.similarity-topic}")
    private String similarityTopic;

    private volatile boolean running = true;

    @PostConstruct
    public void runConsumer() {
        Thread thread = new Thread(this::start);
        thread.setDaemon(true);
        thread.start();
    }

    public void start() {

        KafkaConsumer<Long, SpecificRecordBase> consumer = kafkaConsumerFactory.create();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down, calling consumer.wakeup()");
            running = false;
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(List.of(actionTopic));
            log.info("Subscribed to topic {}", actionTopic);

            while (running) {
                log.debug("Waiting for messages...");
                ConsumerRecords<Long, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                if (!records.isEmpty()) {
                    log.info("Received {} messages", records.count());
                    for (ConsumerRecord<Long, SpecificRecordBase> record : records) {
                        UserActionAvro action = (UserActionAvro) record.value();
                        log.info("Processing user action: {}", action);

                        aggregationService.updateSimilarity(action)
                                .forEach(similarity -> producer.send(similarity, similarityTopic));

                        log.info("User event {} processed", action);
                    }
                    log.info("Performing async offset commit");
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException e) {
            if (running) {
                log.error("Unexpected WakeupException during operation", e);
            } else {
                log.info("WakeupException received during shutdown");
            }
        } catch (Exception e) {
            log.error("Error during user event processing", e);
        } finally {
            try {
                log.info("Flushing producer buffers");
                producer.flush();

                log.info("Committing offsets before shutdown");
                consumer.commitAsync();
            } catch (Exception e) {
                log.error("Error during flush and commit", e);
            } finally {
                log.info("Closing consumer");
                consumer.close();

                log.info("Closing producer");
                producer.close();
            }
        }
    }
}