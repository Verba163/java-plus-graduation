package ru.practicum.collector.producer;

import jakarta.annotation.PreDestroy;
import kafka.serialization.GeneralAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaProducer<Long, SpecificRecordBase> producer;

    public KafkaProducerService(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        this.producer = new KafkaProducer<>(createProducerConfig(bootstrapServers));
    }

    private Properties createProducerConfig(String bootstrapServers) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        return config;
    }

    public void send(SpecificRecordBase action, Long eventId, Instant timestamp, String topic) {

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                eventId,
                action
        );

        producer.send(record);
    }

    public void flush() {
        producer.flush();
    }

    @PreDestroy
    public void close() {
        log.info("Closing Kafka producer");
        producer.flush();
        producer.close(Duration.ofSeconds(5));
    }
}