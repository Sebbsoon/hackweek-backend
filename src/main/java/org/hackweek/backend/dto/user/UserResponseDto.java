package org.hackweek.backend.dto.user;

import org.hackweek.backend.dto.gallery.GalleryResponseDto;
import java.util.List;
import java.time.Instant;

public record UserResponseDto(
        Long id,
        String clerkUserId,
        String username,
        String firstName,
        String lastName,
        String description,
        List<GalleryResponseDto> galleries,
        Instant createdAt) {
}