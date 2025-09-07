package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServerServiceImpl implements StatServerService {
    private final StatServerRepository statServerRepository;
    private final StatMapper statMapper;

    @Override
    public void hit(StatHitDto statHitDto) {
        Stat stat = statMapper.toStatEntity(statHitDto);
        statServerRepository.save(stat);
    }

    @Override
    public List<StatViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<StatViewDto> result;

        if (uris != null && !uris.isEmpty() && unique) {
            result = statServerRepository.getStats(start, end, uris, true);
        } else if (uris != null && !uris.isEmpty()) {
            result = statServerRepository.getStats(start, end, uris);
        } else if (unique) {
            result = statServerRepository.getStats(start, end, true);
        } else {
            result = statServerRepository.getStats(start, end);
        }

        return result;
    }
}
