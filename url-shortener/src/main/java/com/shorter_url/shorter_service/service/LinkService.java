package com.shorter_url.shorter_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shorter_url.shorter_service.DTO.*;
import com.shorter_url.shorter_service.Entity.Link;
import com.shorter_url.shorter_service.Entity.OutboxEvent;
import com.shorter_url.shorter_service.DTO.OutboxStatus;
import com.shorter_url.shorter_service.client.AnalyticsClient;
import com.shorter_url.shorter_service.configuration.AppProperties;
import com.shorter_url.shorter_service.event.LinkClickedEvent;
import com.shorter_url.shorter_service.exception.LinkExpiredException;
import com.shorter_url.shorter_service.exception.LinkNotFoundException;
import com.shorter_url.shorter_service.repository.LinkRepository;
import com.shorter_url.shorter_service.repository.OutboxRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

    private final MeterRegistry meterRegistry;
    private Counter createdLinksCounter;
    private Counter redirectsCounter;

    private final String LINK_NOT_FOUND = "Ссылка не найдена!";

    private final LinkRepository repository;
    private final AppProperties properties;
    private final AnalyticsClient client;

    private final OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        createdLinksCounter = meterRegistry.counter("shortener_links_created_total");
        redirectsCounter = meterRegistry.counter("shortener_redirects_total");
    }

    @Transactional
    public ShortLinkResponse createLink(CreateLinkRequest request){

        log.info("Starting short link creation");

        for (int i = 0; i < 5; i++) {

            Link link = new Link();
            link.setOriginalUrl(request.originalUrl());
            link.setShortCode(UUID.randomUUID().toString().substring(0, 6));
            link.setCreatedAt(LocalDateTime.now());
            link.setExpiresAt(LocalDateTime.now().plusDays(30));
            link.setClicks(0L);

            try {
                repository.save(link);

                createdLinksCounter.increment();

                log.info("Short link successfully created. shortCode={}", link.getShortCode());

                return new ShortLinkResponse(
                        link.getShortCode(),
                        properties.getBaseUrl() + "/" + link.getShortCode()
                );

            } catch (DataIntegrityViolationException e) {
                log.warn("Short code collision. Retrying...");
            }
        }

        throw new RuntimeException("Failed to generate unique short code.");
    }

    @Transactional
    public OriginalLinkResponse getOriginalLink(String shortCode, String userAgent){

        log.info("Поиск ссылки {} в PostgreSQL", shortCode);

        Link link = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(LINK_NOT_FOUND));

        if(link.getExpiresAt() != null &&
                link.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new LinkExpiredException(shortCode);
        }

        LinkClickedEvent event = new LinkClickedEvent(
                link.getShortCode(),
                link.getOriginalUrl(),
                LocalDateTime.now(),
                userAgent,
                MDC.get("correlationId"));

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(OutboxEventType.LINK_CLICKED);
        outboxEvent.setAggregateId(shortCode);
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setSentAt(null);

        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxEvent.setPayload(payload);
        } catch (JsonProcessingException exception){
            throw new RuntimeException("Failed to serialize LinkClickedEvent", exception);
        }

        repository.incrementClicks(shortCode);

        outboxRepository.save(outboxEvent);

        log.info("Orginal URL: {}", link.getOriginalUrl());

        redirectsCounter.increment();

        return new OriginalLinkResponse(link.getOriginalUrl());
    }

    public StatisticsResponse getAnalytics(String shortCode){

            return client.getStatistics(shortCode);

    }

    @Transactional(readOnly = true)
    public LinkInformationResponse getInformation(String shortCode){

        Link link = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(LINK_NOT_FOUND));

        LinkInformationResponse informationResponse = new LinkInformationResponse();
        informationResponse.setCreatedAt(link.getCreatedAt());
        informationResponse.setExpiresAt(link.getExpiresAt());
        informationResponse.setOriginalLink(link.getOriginalUrl());

        return informationResponse;
    }

    @Transactional
    public void delete(String shortCode){

        Link link = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(LINK_NOT_FOUND));

        repository.delete(link);
    }
}
