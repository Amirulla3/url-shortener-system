package com.shorter_url.shorter_service.client;

import com.shorter_url.shorter_service.DTO.StatisticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsClient {

    private final WebClient webClient;

    public StatisticsResponse getStatistics(String shortCode){

        try {
            return webClient
                    .get()
                    .uri("/api/analytics/{shortCode}/statistics", shortCode)
                    .retrieve()
                    .bodyToMono(StatisticsResponse.class)
                    .block();
        } catch (WebClientException exception) {

            log.warn("Analytics service is unavailable");

            return new StatisticsResponse(0L);

        }
    }
}
