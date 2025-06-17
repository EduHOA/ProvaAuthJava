package com.edu.atividade.controller;

import com.edu.atividade.dto.UserRegistrationDto;
import com.edu.atividade.model.User;
import com.edu.atividade.model.CustomUserDetails;
import com.edu.atividade.service.UserService;
import com.edu.atividade.dto.UserProfileDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserProfileDto dto = new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateUserProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserRegistrationDto updateDto) {
        User updatedUser = userService.updateUser(currentUser.getId(), updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationDto updateDto) {
        User updatedUser = userService.updateUser(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 