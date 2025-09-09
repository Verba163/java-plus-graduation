package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.StatViewDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerRepository extends JpaRepository<Stat, Long> {
    @Query("""
            SELECT new ru.practicum.dto.StatViewDto(s.app, s.uri, COUNT(*) hits)
            FROM Stat s
            WHERE s.timestamp BETWEEN ?1 AND ?2
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<StatViewDto> getStats(LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.dto.StatViewDto(s.app, s.uri, COUNT(*) hits)
            FROM Stat s
            WHERE s.timestamp BETWEEN ?1 AND ?2
                AND s.uri IN (?3)
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<StatViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("""
            SELECT new ru.practicum.dto.StatViewDto(s.app, s.uri, COUNT(s.ip) hits)
            FROM (
                SELECT ss.app app, ss.uri uri, ss.ip ip
                FROM Stat ss
                WHERE ss.timestamp BETWEEN ?1 AND ?2
                GROUP BY ss.app, ss.uri, ss.ip
            ) AS s
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<StatViewDto> getStats(LocalDateTime start, LocalDateTime end, boolean unique);

    @Query("""
            SELECT new ru.practicum.dto.StatViewDto(s.app, s.uri, COUNT(s.ip) hits)
            FROM (
                SELECT ss.app app, ss.uri uri, ss.ip ip
                FROM Stat ss
                WHERE ss.timestamp BETWEEN ?1 AND ?2
                    AND ss.uri IN (?3)
                GROUP BY ss.app, ss.uri, ss.ip
            ) AS s
            GROUP BY s.app, s.uri
            ORDER BY hits DESC
            """)
    List<StatViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
