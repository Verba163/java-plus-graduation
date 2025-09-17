package ru.practicum.ewm.events.service.publics.params.comparing;

import ru.practicum.ewm.events.enums.SortingEvents;
import ru.practicum.ewm.events.model.Event;

import java.util.Comparator;
import java.util.List;

public interface EventSorter {
    Comparator<Event> getComparator(SortingEvents sort, List<Long> eventIds);
}