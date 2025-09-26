package ru.practicum.events.service.publics.params.comparing;

import ru.practicum.events.model.Event;
import ru.practicum.interaction.events.enums.SortingEvents;

import java.util.Comparator;
import java.util.List;

public interface EventSorter {
    Comparator<Event> getComparator(SortingEvents sort, List<Long> eventIds);
}