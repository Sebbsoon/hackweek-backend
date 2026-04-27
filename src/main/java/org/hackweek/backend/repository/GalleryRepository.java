package org.hackweek.backend.repository;

import org.hackweek.backend.model.Gallery;
import org.springframework.data.repository.ListCrudRepository;

public interface GalleryRepository extends ListCrudRepository<Gallery, Long> {
    
}
