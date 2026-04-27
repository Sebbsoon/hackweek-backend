package org.hackweek.backend.repository;

import java.util.List;
import org.hackweek.backend.model.Image;
import org.springframework.data.repository.ListCrudRepository;

public interface ImageRepository extends ListCrudRepository<Image, Long> {
    List<Image> findByGalleryId(Long galleryId);
}
