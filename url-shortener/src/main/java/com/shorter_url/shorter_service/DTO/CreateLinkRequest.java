package com.shorter_url.shorter_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CreateLinkRequest(@NotBlank @URL @Size(max = 2048) String originalUrl) {
}
