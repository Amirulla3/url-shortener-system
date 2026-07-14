package analytics_service.consumer;

import analytics_service.event.LinkClickedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class DlqConsumer {

    @KafkaListener(topics = "link-clicks-dlt")
    public void consume(LinkClickedEvent event){

        log.error("========== DLQ MESSAGE ==========");
        log.error("Short code     : {}", event.shortCode());
        log.error("Original URL   : {}", event.originalUrl());
        log.error("Clicked at     : {}", event.clickedAt());
        log.error("User-Agent     : {}", event.userAgent());
        log.error("Correlation Id : {}", event.correlationId());
        log.error("=================================");

    }
}
