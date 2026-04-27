package org.hackweek.backend.repository;

import java.util.Optional;

import org.hackweek.backend.model.User;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, Long> {
    Optional<User> findByClerkUserId(String clerkUserId);
}
