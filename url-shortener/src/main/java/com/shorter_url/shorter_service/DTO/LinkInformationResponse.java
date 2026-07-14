package com.shorter_url.shorter_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkInformationResponse {

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String originalLink;
}
