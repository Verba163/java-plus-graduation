package ru.practicum.collector.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.collector.producer.KafkaProducerService;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.action.UserActionProto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserActionHandler {

    private final KafkaProducerService kafkaProducer;

    @Value("${kafka.action-topic}")
    String topic;

    public void handle(UserActionProto proto) {
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        UserActionAvro avro = UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(getActionTypeAvro(proto.getActionType()))
                .setTimestamp(timestamp)
                .build();
        kafkaProducer.send(avro, proto.getEventId(), timestamp, topic);
    }

    private ActionTypeAvro getActionTypeAvro(ActionTypeProto actionType) {

        if (actionType == null) {
            throw new IllegalArgumentException("Action Type cannot be null");
        }

        return switch (actionType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            default -> throw new IllegalArgumentException(String.format(
                    "Unknown type of action type: %s", actionType
            ));
        };
    }
}