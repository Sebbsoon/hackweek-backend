package org.hackweek.backend.dto.user;

public record CreateUserRequestDto(
        String clerkUserId,
        String username,
        String firstName,
        String lastName,
        String description) {
}