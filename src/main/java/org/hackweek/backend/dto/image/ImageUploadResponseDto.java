package org.hackweek.backend.dto.image;

public record ImageUploadResponseDto(
    Long id,
    Long galleryId,
    String title,
    String description,
    String fileName,
    String contentType,
    long size,
    String objectPath,
    String url
) {}