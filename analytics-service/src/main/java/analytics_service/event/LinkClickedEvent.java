package analytics_service.event;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
public record LinkClickedEvent(String shortCode,
                               String originalUrl,
                               LocalDateTime clickedAt,
                               String userAgent,
                               String correlationId) {
}
