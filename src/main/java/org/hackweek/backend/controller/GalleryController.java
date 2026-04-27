package org.hackweek.backend.controller;

import org.hackweek.backend.dto.gallery.CreateGalleryRequestDto;
import org.hackweek.backend.dto.gallery.GalleryResponseDto;
import org.hackweek.backend.model.Gallery;
import org.hackweek.backend.service.GalleryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/galleries")
public class GalleryController {
    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @PostMapping
    public ResponseEntity<GalleryResponseDto> createGallery(
        @RequestBody CreateGalleryRequestDto request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Gallery galleryToCreate = new Gallery();
        galleryToCreate.setTitle(request.title());
        galleryToCreate.setDescription(request.description());

        Gallery created = galleryService.createGallery(galleryToCreate, jwt.getSubject());

        GalleryResponseDto response = new GalleryResponseDto(
            created.getId(),
            created.getOwner() != null ? created.getOwner().getId() : null,
            created.getTitle(),
            created.getDescription(),
            created.getOwner() != null ? created.getOwner().getClerkUserId() : null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGallery(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt
    ) {
        galleryService.deleteGallery(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
