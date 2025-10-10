package ru.practicum.analyzer.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void handle(UserActionAvro userActionAvro) {
        try {
            log.info("Handling user action for userId: {}, eventId: {}", userActionAvro.getUserId(), userActionAvro.getEventId());

            Optional<UserAction> userActionOptional = userActionRepository.findByUserIdAndEventId(
                    userActionAvro.getUserId(), userActionAvro.getEventId());

            if (userActionOptional.isPresent()) {
                UserAction userAction = userActionOptional.get();
                Double currentWeight = toWeight(userAction.getActionType());
                Double newWeight = toWeight(ActionType.valueOf(userActionAvro.getActionType().name()));
                log.debug("Existing action found. Current weight: {}, New weight: {}", currentWeight, newWeight);

                if (newWeight > currentWeight) {
                    log.info("Updating action type for userId: {}, eventId: {}", userActionAvro.getUserId(), userActionAvro.getEventId());
                    userAction.setActionType(ActionType.valueOf(userActionAvro.getActionType().name()));
                    userAction.setTimestamp(userActionAvro.getTimestamp());
                    userActionRepository.save(userAction);
                } else {
                    log.debug("No update needed. Existing action has equal or higher weight.");
                }
            } else {
                log.info("No existing action found. Creating new action for userId: {}, eventId: {}", userActionAvro.getUserId(), userActionAvro.getEventId());

                UserAction userAction = UserAction.builder()
                        .userId(userActionAvro.getUserId())
                        .eventId(userActionAvro.getEventId())
                        .actionType(ActionType.valueOf(userActionAvro.getActionType().name()))
                        .timestamp(userActionAvro.getTimestamp())
                        .build();

                userActionRepository.save(userAction);
                log.info("New user action saved successfully for userId: {}, eventId: {}", userActionAvro.getUserId(), userActionAvro.getEventId());
            }
        } catch (Exception e) {
            log.error("Error occurred while handling user action for userId: {}, eventId: {}", userActionAvro.getUserId(), userActionAvro.getEventId(), e);
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