package com.shorter_url.shorter_service.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreateLinkRequest(@NotBlank String originalUrl) {
}
