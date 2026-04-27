package org.hackweek.backend.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.hackweek.backend.dto.image.ImageUploadRequestDto;
import org.hackweek.backend.dto.image.ImageUploadResponseDto;
import org.hackweek.backend.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/images")
    public ResponseEntity<String> getAllImages() {
        return ResponseEntity.ok("List of images");
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<String> getImage(@PathVariable String id) {
        return ResponseEntity.ok("Image data");
    }

    @PostMapping("/images")
    public ResponseEntity<String> createImage() {
        return ResponseEntity.ok("Image created");
    }

    @PutMapping("/images/{id}")
    public ResponseEntity<String> updateImage(@PathVariable String id) {
        return ResponseEntity.ok("Image updated");
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable String id) {
        return ResponseEntity.ok("Image deleted");
    }

    @PostMapping(
            value = "/galleries/{galleryId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> addImageToGallery(
            @PathVariable Long galleryId,
            @ModelAttribute ImageUploadRequestDto request
    ) {
        try {
            if (request.getFile() == null || request.getFile().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is required"));
            }

            ImageService.UploadResult uploaded =
                    imageService.uploadToGalleryBucket(galleryId, request.getFile());

            ImageUploadResponseDto response = new ImageUploadResponseDto(
                    uploaded.imageId(),
                    galleryId,
                    request.getTitle(),
                    request.getDescription(),
                    request.getFile().getOriginalFilename(),
                    request.getFile().getContentType(),
                    request.getFile().getSize(),
                    uploaded.objectPath(),
                    uploaded.publicUrl()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload interrupted"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}