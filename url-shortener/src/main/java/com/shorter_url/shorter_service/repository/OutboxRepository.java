package com.shorter_url.shorter_service.repository;

import com.shorter_url.shorter_service.Entity.OutboxEvent;
import com.shorter_url.shorter_service.DTO.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    long countByStatus(OutboxStatus status);

}
