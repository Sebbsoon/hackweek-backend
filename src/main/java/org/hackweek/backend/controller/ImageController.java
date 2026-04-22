package org.hackweek.backend.controller;

import org.hackweek.backend.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
public class ImageController {

private final ImageService imageService; 

    public ImageController(ImageService imageService) {
        this.imageService = imageService;   
    }

    @GetMapping
    public ResponseEntity<String> getAllImages() {
        // Placeholder for logic to retrieve all images
        return ResponseEntity.ok("List of images");
    }
    @GetMapping("{id}")
    public ResponseEntity<String> getImage(@PathVariable String id) {
        // Placeholder for image retrieval logic
        return ResponseEntity.ok("Image data");
    }

    @PostMapping
    public ResponseEntity<String> createImage() {
        // Placeholder for image creation logic
        return ResponseEntity.ok("Image created");
    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateImage(@PathVariable String id) {
        // Placeholder for image update logic
        return ResponseEntity.ok("Image updated");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteImage(@PathVariable String id) {
        // Placeholder for image deletion logic
        return ResponseEntity.ok("Image deleted");
    }
}