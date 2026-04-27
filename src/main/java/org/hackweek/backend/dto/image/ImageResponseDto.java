package org.hackweek.backend.dto.image;

public record ImageResponseDto(
    Long id,
    Long galleryId,
    String title,
    String url,
    String thumbnailUrl
) {}