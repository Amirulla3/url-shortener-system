package com.shorter_url.shorter_service.service.outbox_service;

import com.shorter_url.shorter_service.DTO.OutboxStatus;
import com.shorter_url.shorter_service.repository.OutboxRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxMetrics {

    private final MeterRegistry meterRegistry;
    private final OutboxRepository repository;

    @PostConstruct
    public void registerMetrics(){
        Gauge.builder("outbox_pending_count",
                repository,
                repo -> repo.countByStatus(OutboxStatus.PENDING))
                .description("Number of pending outbox events")
                .register(meterRegistry);
    }
}
