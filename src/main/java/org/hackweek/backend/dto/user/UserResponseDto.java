package org.hackweek.backend.dto.user;

import org.hackweek.backend.model.Gallery;
import java.util.List;
import java.time.Instant;

public record UserResponseDto(
        Long id,
        String clerkUserId,
        String username,
        String firstName,
        String lastName,
        String description,
        List<Gallery> galleries,
        Instant createdAt) {
}