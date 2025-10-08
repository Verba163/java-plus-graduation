package ru.practicum.ewm.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.action.UserActionProto;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;

import java.time.Instant;

@Slf4j
@Service
public class UserActionClient {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub userClient;

    public void collectUserAction(long userId, long eventId, ActionTypeProto actionType, Instant instant) {
        UserActionProto request = buildUserActionRequest(userId, eventId, actionType, instant);

        try {
            userClient.collectUserAction(request);
            log.debug("User action sent successfully: {}", request);
        } catch (Exception e) {
            log.error("Failed to send user action (userId={}, eventId={}): {}", userId, eventId, e.getMessage(), e);
        }
    }

    private UserActionProto buildUserActionRequest(long userId, long eventId, ActionTypeProto actionType, Instant instant) {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();

        return UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();
    }
}
