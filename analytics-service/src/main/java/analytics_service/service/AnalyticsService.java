package analytics_service.service;

import analytics_service.DTO.StatisticsResponse;
import analytics_service.entity.ClickEvent;
import analytics_service.event.LinkClickedEvent;
import analytics_service.repository.ClickEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsService {

    private final ClickEventRepository repository;

    public void process(LinkClickedEvent event){

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setShortCode(event.shortCode());
        clickEvent.setOriginalUrl(event.originalUrl());
        clickEvent.setClickedAt(event.clickedAt());
        clickEvent.setUserAgent(event.userAgent());
        clickEvent.setCorrelationId(event.correlationId());

        repository.save(clickEvent);
    }

    public StatisticsResponse getStatistics(String shortCode){

        return new StatisticsResponse(repository.countByShortCode(shortCode));

    }
}
