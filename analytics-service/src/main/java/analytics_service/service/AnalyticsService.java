package analytics_service.service;

import analytics_service.DTO.StatisticsResponse;
import analytics_service.entity.ClickEvent;
import analytics_service.event.LinkClickedEvent;
import analytics_service.repository.ClickEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AnalyticsService {

    private final ClickEventRepository repository;

    public void process(LinkClickedEvent event){

        log.info(
                "Processing click event. shortCode={}, correlationId={}",
                event.shortCode(),
                event.correlationId()
        );

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setShortCode(event.shortCode());
        clickEvent.setOriginalUrl(event.originalUrl());
        clickEvent.setClickedAt(event.clickedAt());
        clickEvent.setUserAgent(event.userAgent());
        clickEvent.setCorrelationId(event.correlationId());

        repository.save(clickEvent);

        log.info(
                "Processing click event. shortCode={}, correlationId={}",
                event.shortCode(),
                event.correlationId()
        );
    }

    public StatisticsResponse getStatistics(String shortCode){

        log.info(
                "Getting statistics. shortCode={}",
                shortCode
        );

        long clicks = repository.countByShortCode(shortCode);

        log.info(
                "Statistics loaded. shortCode={}, clicks={}",
                shortCode,
                clicks
        );

        return new StatisticsResponse(clicks);

    }
}
