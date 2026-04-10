package com.smartevent.user_service.controller;

import com.smartevent.user_service.dto.request.UpdateUserRequest;
import com.smartevent.user_service.dto.response.UserResponse;
import com.smartevent.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getCurrentUser(authentication.getName())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.updateUser(id, request, authentication.getName())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            Authentication authentication
    ) {
        userService.deleteUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ADMIN only
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}