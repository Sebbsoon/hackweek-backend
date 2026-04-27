package org.hackweek.backend.controller;

import java.net.URI;
import java.util.List;

import org.hackweek.backend.dto.user.CreateUserRequestDto;
import org.hackweek.backend.dto.user.UserResponseDto;
import org.hackweek.backend.model.User;
import org.hackweek.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers()
            .stream()
            .map(this::toResponseDto)
            .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toResponseDto(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody CreateUserRequestDto request) {
        User user = new User();
        user.setClerkUserId(request.clerkUserId());

        User newUser = userService.createUser(user);
        return ResponseEntity
            .created(URI.create("/api/users/" + newUser.getId()))
            .body(toResponseDto(newUser));
    }

    private UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getClerkUserId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getDescription(),
            user.getGalleries(),
            user.getCreatedAt()
        );
    }
}
