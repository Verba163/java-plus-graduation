package ru.practicum.ewm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class StatClientImpl implements StatClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RestClient restClient;
    private final ObjectMapper mapper;

    public StatClientImpl(RestClient restClient, ObjectMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    public void hit(StatHitDto statHitDto) {

        String jsonBody;
        try {
            jsonBody = mapper.writeValueAsString(statHitDto);
            restClient.post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonBody)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.error("Ошибка при записи hit", ex);
            throw new RuntimeException("Ошибка при записи hit: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<StatViewDto> getStat(LocalDateTime start, LocalDateTime end,
                                     List<String> uris, Boolean unique) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                    .path("/stats")
                    .queryParam("start", start.format(FORMATTER))
                    .queryParam("end", end.format(FORMATTER));

            if (uris != null && !uris.isEmpty()) {
                builder.queryParam("uris", uris);
            }

            if (unique != null) {
                builder.queryParam("unique", unique);
            }

            String uri = builder.build().toUriString();

            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatViewDto>>() {
                    });
        } catch (RestClientException e) {
            log.error("Ошибка при запросе на получение статистики", e);
            return new ArrayList<>();
        }
    }
}
