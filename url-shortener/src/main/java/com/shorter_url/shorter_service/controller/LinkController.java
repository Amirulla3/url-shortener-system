package com.shorter_url.shorter_service.controller;

import com.shorter_url.shorter_service.DTO.*;
import com.shorter_url.shorter_service.service.LinkService;
import com.shorter_url.shorter_service.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;
    private final RateLimitService rateLimitService;

    @PostMapping("/api/links")
    public ResponseEntity<ShortLinkResponse> createShortUrl(@Valid @RequestBody CreateLinkRequest request,
                                                         HttpServletRequest httpServletRequest){

        String ip = httpServletRequest.getRemoteAddr();

        if(rateLimitService.isRateLimited(ip)){
            return ResponseEntity.
                    status(HttpStatus.TOO_MANY_REQUESTS)
                    .build();
        }

        ShortLinkResponse response = linkService.createLink(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode,
                                         @RequestHeader("User-Agent") String userAgent
    ){

        OriginalLinkResponse response = linkService.getOriginalLink(shortCode, userAgent);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(response.originalLink()))
                .build();
    }

    @GetMapping("/api/links/{shortCode}/analytics")
    public ResponseEntity<StatisticsResponse> statistics(@PathVariable String shortCode){

        StatisticsResponse response = linkService.getAnalytics(shortCode);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/links/{shortCode}")
    public ResponseEntity<LinkInformationResponse> information(@PathVariable String shortCode){

        LinkInformationResponse response = linkService.getInformation(shortCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/api/links/{shortCode}")
    public ResponseEntity<Void> delete(@PathVariable String shortCode){

        linkService.delete(shortCode);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }


}
