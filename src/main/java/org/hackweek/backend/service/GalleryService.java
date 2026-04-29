package org.hackweek.backend.service;

import org.hackweek.backend.model.Gallery;
import org.hackweek.backend.model.User;
import org.hackweek.backend.repository.GalleryRepository;
import org.hackweek.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

@Service
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;

    public GalleryService(GalleryRepository galleryRepository, UserRepository userRepository) {
        this.galleryRepository = galleryRepository;
        this.userRepository = userRepository;
    }

    public Gallery createGallery(Gallery gallery, String clerkUserId) {
        User owner = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        gallery.setOwner(owner);
        return galleryRepository.save(gallery);
    }

    public Gallery deleteGallery(Long galleryId, String clerkUserId) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new IllegalArgumentException("Gallery not found"));

        String ownerClerkId = gallery.getOwner().getClerkUserId();
        if (!ownerClerkId.equals(clerkUserId)) {
            throw new AccessDeniedException("Not allowed to delete this gallery");
        }

         galleryRepository.delete(gallery);
        return gallery;
    }
}
