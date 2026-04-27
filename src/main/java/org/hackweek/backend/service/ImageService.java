package org.hackweek.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hackweek.backend.client.SupabaseStorageClient;
import org.hackweek.backend.dto.image.ImageResponseDto;
import org.hackweek.backend.model.Gallery;
import org.hackweek.backend.model.Image;
import org.hackweek.backend.repository.GalleryRepository;
import org.hackweek.backend.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

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
            String title)
            throws IOException, InterruptedException {
        log.info("Starting upload to gallery bucket: galleryId={}, originalFilename={}", galleryId,
                file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        log.info("Fetching gallery from DB: galleryId={}", galleryId);
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found: " + galleryId));

        log.info("Gallery fetched successfully: galleryId={}, galleryTitle={}", galleryId, gallery.getTitle());

        String original = Objects.requireNonNullElse(file.getOriginalFilename(), "upload.bin");
        String extension = getExtension(original);
        String safeName = UUID.randomUUID() + extension;
        String objectPath = "galleries/" + galleryId + "/" + safeName;

        log.info("Uploading file to Supabase Storage: bucket={}, objectPath={}", bucket, objectPath);
        SupabaseStorageClient.UploadResult uploaded = supabaseStorageClient.upload(bucket, objectPath, file);

        Image image = new Image();
        image.setGallery(gallery);
        image.setTitle(title);
        image.setUrl(uploaded.publicUrl());
        image.setThumbnailUrl(uploaded.publicUrl() + "?width=200&resize=cover");

        log.info("Saving image metadata to DB: galleryId={}, title={}, url={}", galleryId, title, uploaded.publicUrl());
        Image saved = imageRepository.save(image);
        log.info("DB save ok imageId={}", saved.getId());

        return new UploadResult(saved.getId(), uploaded.objectPath(), uploaded.publicUrl());
    }
    @Transactional
    public void deleteImage(Long imageId) {
        log.info("Deleting image: imageId={}", imageId);

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

        String objectPath = extractObjectPathFromPublicUrl(image.getUrl(), bucket);
        if (objectPath.isBlank()) {
            throw new IllegalStateException("Could not resolve object path for imageId=" + imageId);
        }

        supabaseStorageClient.delete(bucket, objectPath);
        log.info("Deleted from bucket: {}", objectPath);

        imageRepository.delete(image);
        log.info("Deleted from database: imageId={}", imageId);
    }

    private static String extractObjectPathFromPublicUrl(String publicUrl, String bucket) {
        if (publicUrl == null || bucket == null) return "";
        String marker = "/storage/v1/object/public/" + bucket + "/";
        int idx = publicUrl.indexOf(marker);
        if (idx >= 0) {
            return publicUrl.substring(idx + marker.length());
        }
        int pos = publicUrl.indexOf("/" + bucket + "/");
        if (pos >= 0) {
            return publicUrl.substring(pos + bucket.length() + 2);
        }
        int lastSlash = publicUrl.lastIndexOf('/');
        return lastSlash >= 0 ? publicUrl.substring(lastSlash + 1) : publicUrl;
    }

    private static String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        return idx >= 0 ? filename.substring(idx) : "";
    }

    public record UploadResult(Long imageId, String objectPath, String publicUrl) {
    }

    public List<ImageResponseDto> getImagesByGalleryId(Long galleryId) {
        return imageRepository.findByGalleryId(galleryId).stream()
                .map(image -> new ImageResponseDto(
                        image.getId(),
                        galleryId,
                        image.getTitle(),
                        image.getUrl(),
                        image.getThumbnailUrl()))
                .toList();
    }
}
