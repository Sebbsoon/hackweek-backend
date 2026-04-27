package org.hackweek.backend.dto.gallery;

public record GalleryResponseDto(
    Long id,
    Long userId,
    String title,
    String description,
    String ownerClerkId
) {}