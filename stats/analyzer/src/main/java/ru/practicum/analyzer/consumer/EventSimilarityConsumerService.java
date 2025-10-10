package ru.practicum.analyzer.consumer;

import jakarta.annotation.PreDestroy;
import kafka.desarialization.EventSimilarityDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;

@Slf4j
@Service
public class EventSimilarityConsumerService {

    private final KafkaConsumer<Long, SpecificRecordBase> kafkaConsumer;

    public EventSimilarityConsumerService(@Value("${kafka.bootstrap-servers}") String bootstrapServers,
                                          @Value("${kafka.group-id.similarity}") String groupId,
                                          @Value("${kafka.auto-commit}") boolean autoCommit) {
        this.kafkaConsumer = new KafkaConsumer<>(createConsumerConfig(bootstrapServers, groupId, autoCommit));
    }

    private Properties createConsumerConfig(String bootstrapServers, String groupId, boolean autoCommit) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, EventSimilarityDeserializer.class.getName());
        return props;
    }

    public ConsumerRecords<Long, SpecificRecordBase> poll(Duration duration) {
        try {
            return kafkaConsumer.poll(duration);
        } catch (WakeupException e) {
            log.info("Wakeup exception triggered to stop consumer: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An error occurred while calling poll: ", e);
            throw e;
        }
    }

    public void subscribe(Collection<String> topics) {
        kafkaConsumer.subscribe(topics);
    }

    public void commitAsync() {
        kafkaConsumer.commitAsync((offsets, exception) -> {
            if (exception != null) {
                log.error("Error during asynchronous commit of offsets: {} on partitions: {}", offsets, exception.getMessage(), exception);
            } else {
                log.info("Asynchronous commit was successful. Offsets: {}", offsets);
            }
        });
    }

    public void wakeup() {
        kafkaConsumer.wakeup();
    }

    @PreDestroy
    public void close() {
        kafkaConsumer.close();
    }
}