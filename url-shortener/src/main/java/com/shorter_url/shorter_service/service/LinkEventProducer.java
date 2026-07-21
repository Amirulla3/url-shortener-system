package com.shorter_url.shorter_service.service;

import com.shorter_url.shorter_service.event.LinkClickedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
public class LinkEventProducer {

    private static final String TOPIC = "link-clicks";

    private final KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;

    public void send(LinkClickedEvent event){
        kafkaTemplate.send(TOPIC, event.shortCode(), event)
                .whenComplete((result, ex) ->{
                    if(ex != null){
                        log.error("Failed to send LinkClickedEvent", ex);
                        return;
                    }

                    log.info(
                            "Message sent. topic={}, partition={}, offset={}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }
}
