package org.hackweek.backend.controller;

import org.hackweek.backend.model.Gallery;
import org.hackweek.backend.service.GalleryService;
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

    public GalleryController(org.hackweek.backend.service.GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @PostMapping
    public Gallery createGallery(@RequestBody Gallery gallery, @AuthenticationPrincipal Jwt jwt) {
        return galleryService.createGallery(gallery, jwt.getSubject());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGallery(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        galleryService.deleteGallery(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

}
