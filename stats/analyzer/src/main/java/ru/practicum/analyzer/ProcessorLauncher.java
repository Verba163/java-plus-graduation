package ru.practicum.analyzer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.runner.EventSimilarityProcessor;
import ru.practicum.analyzer.runner.UserActionProcessor;

@Component
public class ProcessorLauncher implements CommandLineRunner {
    private final UserActionProcessor userActionProcessor;
    private final EventSimilarityProcessor eventSimilarityProcessor;

    public ProcessorLauncher(UserActionProcessor userActionProcessor,
                             EventSimilarityProcessor eventSimilarityProcessor) {
        this.userActionProcessor = userActionProcessor;
        this.eventSimilarityProcessor = eventSimilarityProcessor;
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(userActionProcessor).start();
        new Thread(eventSimilarityProcessor).start();
    }
}