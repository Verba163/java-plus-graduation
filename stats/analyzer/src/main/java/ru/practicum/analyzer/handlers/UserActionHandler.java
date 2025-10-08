package ru.practicum.analyzer.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.enums.ActionType;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionHandler {

    private final UserActionRepository userActionRepository;

    public void handle(UserActionAvro userActionAvro) {

        Optional<UserAction> userActionOptional = userActionRepository.findByUserIdAndEventId(userActionAvro.getUserId(),
                userActionAvro.getEventId());

        if (userActionOptional.isPresent()) {
            UserAction userAction = userActionOptional.get();
            Double weight = toWeight(userAction.getActionType());
            Double newWeight = toWeight(ActionType.valueOf(userActionAvro.getActionType().name()));

            if (newWeight > weight) {
                userAction.setActionType(ActionType.valueOf(userActionAvro.getActionType().name()));
                userAction.setTimestamp(userActionAvro.getTimestamp());
                userActionRepository.save(userAction);
            }
        } else {
            UserAction userAction = UserAction.builder()
                    .userId(userActionAvro.getUserId())
                    .eventId(userActionAvro.getEventId())
                    .actionType(ActionType.valueOf(userActionAvro.getActionType().name()))
                    .timestamp(userActionAvro.getTimestamp())
                    .build();
            userActionRepository.save(userAction);
        }
    }

    private Double toWeight(ActionType actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}