package org.hackweek.backend.dto.gallery;

public record CreateGalleryRequestDto(
    String title,
    String description
) {}