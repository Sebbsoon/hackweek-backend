package org.hackweek.backend.service;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.hackweek.backend.client.SupabaseStorageClient;
import org.hackweek.backend.model.Gallery;
import org.hackweek.backend.model.Image;
import org.hackweek.backend.repository.GalleryRepository;
import org.hackweek.backend.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private final SupabaseStorageClient supabaseStorageClient;
    private final ImageRepository imageRepository;
    private final GalleryRepository galleryRepository;

    @Value("${supabase.bucket}")
    private String bucket;

    public ImageService(
            SupabaseStorageClient supabaseStorageClient,
            ImageRepository imageRepository,
            GalleryRepository galleryRepository) {
        this.supabaseStorageClient = supabaseStorageClient;
        this.imageRepository = imageRepository;
        this.galleryRepository = galleryRepository;
    }

    @Transactional
    public UploadResult uploadToGalleryBucket(
            Long galleryId,
            MultipartFile file,
            String title,
            String description) throws IOException, InterruptedException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        Gallery galleryEntity = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + galleryId));

        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "upload.bin");
        String extension = getExtension(original);
        String safeName = UUID.randomUUID() + extension;

        String objectPath = "galleries/" + galleryId + "/" + safeName;

        SupabaseStorageClient.UploadResult uploaded = supabaseStorageClient.upload(bucket, objectPath, file);

        Image image = new Image();
        image.setGallery(galleryEntity);
        image.setTitle(title);
        image.setUrl(uploaded.publicUrl());

        Image saved = imageRepository.save(image);

        return new UploadResult(saved.getId(), uploaded.objectPath(), uploaded.publicUrl());
    }

    public UploadResult uploadToGalleryBucket(Long galleryId, MultipartFile file)
            throws IOException, InterruptedException {
        return uploadToGalleryBucket(galleryId, file, null, null);
    }

    private static String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx) : "";
    }

    public record UploadResult(Long imageId, String objectPath, String publicUrl) {
    }
}
