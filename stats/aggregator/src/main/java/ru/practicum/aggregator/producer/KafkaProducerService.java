package ru.practicum.aggregator.producer;

import jakarta.annotation.PreDestroy;
import kafka.serialization.GeneralAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.Properties;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    public KafkaProducerService(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        this.producer = new KafkaProducer<>(createProducerConfig(bootstrapServers));
    }

    private Properties createProducerConfig(String bootstrapServers) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        return config;
    }

    public void send(EventSimilarityAvro similarity, String topic) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                similarity.getTimestamp().toEpochMilli(),
                similarity.getEventA() + "-" + similarity.getEventB(),
                similarity
        );
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Error sending message to topic {}: {}", topic, exception.getMessage(), exception);
            } else {
                log.info("Message successfully sent to topic {}: partition {}, offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
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