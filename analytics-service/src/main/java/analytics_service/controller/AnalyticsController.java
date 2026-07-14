package analytics_service.controller;

import analytics_service.DTO.StatisticsResponse;
import analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("api/analytics/{shortCode}/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics(
            @PathVariable String shortCode)
    {

        StatisticsResponse response = service.getStatistics(shortCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }
}
