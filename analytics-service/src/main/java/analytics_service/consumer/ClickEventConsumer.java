package analytics_service.consumer;

import analytics_service.event.LinkClickedEvent;
import analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClickEventConsumer{

    private final AnalyticsService service;

    @KafkaListener(
            topics = "link-clicks",
            groupId = "analytics-group"
    )
    public void consume(LinkClickedEvent event){

        log.info("Received event: {}", event);

        service.process(event);

    }
}
