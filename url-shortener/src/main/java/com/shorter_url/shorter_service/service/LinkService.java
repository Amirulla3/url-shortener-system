package com.shorter_url.shorter_service.service;

import com.shorter_url.shorter_service.DTO.*;
import com.shorter_url.shorter_service.Entity.Link;
import com.shorter_url.shorter_service.client.AnalyticsClient;
import com.shorter_url.shorter_service.configuration.AppProperties;
import com.shorter_url.shorter_service.event.LinkClickedEvent;
import com.shorter_url.shorter_service.exception.LinkExpiredException;
import com.shorter_url.shorter_service.exception.LinkNotFoundException;
import com.shorter_url.shorter_service.repository.LinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

    private final String LINK_NOT_FOUND = "Ссылка не найдена!";

    private final LinkRepository repository;
    private final AppProperties properties;
    private final LinkEventProducer producer;
    private final AnalyticsClient client;

    public ShortLinkResponse createLink(CreateLinkRequest request){

        Link link = new Link();
        link.setOriginalUrl(request.originalUrl());
        link.setShortCode(UUID.randomUUID().toString().substring(0, 6));
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(30));
        link.setClicks(0L);

        repository.save(link);

        ShortLinkResponse shortLinkResponse = new ShortLinkResponse(
                link.getShortCode(),
                properties.getBaseUrl()+link.getShortCode()
        );

        System.out.println("BaseUrl = " + properties.getBaseUrl());
        System.out.println("ShortUrl = " + properties.getBaseUrl() + link.getShortCode());

        return shortLinkResponse;
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

        link.setClicks(link.getClicks() + 1L);

        producer.send(new LinkClickedEvent(
                link.getShortCode(),
                link.getOriginalUrl(),
                LocalDateTime.now(),
                userAgent,
                UUID.randomUUID().toString()
        ));

        log.info("Orginal URL: {}", link.getOriginalUrl());

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

    @Transactional(readOnly = true)
    @CacheEvict(value = "links", key = "#shortCode")
    public void delete(String shortCode){

        Link link = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(LINK_NOT_FOUND));

        repository.delete(link);
    }
}
