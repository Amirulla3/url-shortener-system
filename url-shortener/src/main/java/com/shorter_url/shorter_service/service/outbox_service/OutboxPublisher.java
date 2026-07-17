package com.shorter_url.shorter_service.service.outbox_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shorter_url.shorter_service.Entity.OutboxEvent;
import com.shorter_url.shorter_service.DTO.OutboxStatus;
import com.shorter_url.shorter_service.event.LinkClickedEvent;
import com.shorter_url.shorter_service.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository repository;

    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;

    @Scheduled(fixedDelay = 2000)
    public void publish(){

        List<OutboxEvent> events = repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if(events.isEmpty()){
            return;
        }

        for (OutboxEvent outboxEvent : events) {
            try{
                LinkClickedEvent event = objectMapper
                        .readValue(outboxEvent.getPayload(), LinkClickedEvent.class);

                kafkaTemplate.send("link-clicks", event).get();

                outboxEvent.setStatus(OutboxStatus.SENT);
                outboxEvent.setSentAt(LocalDateTime.now());

                repository.save(outboxEvent);

                log.info("Outbox event {} successfully published",
                        outboxEvent.getId()
                );
            }
            catch (JsonProcessingException exception){

                log.error(
                        "Cannot deserialize outbox event {}",
                        outboxEvent.getId(),
                        exception);

            } catch (Exception e) {

                log.error(
                "Outbox event {} successfully published",
                        outboxEvent.getId());

            }
        }
    }
}
