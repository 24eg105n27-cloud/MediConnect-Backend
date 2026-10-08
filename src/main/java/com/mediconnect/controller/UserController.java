package com.mediconnect.controller;

import com.mediconnect.config.AuthenticatedUserInterceptor;
import com.mediconnect.model.User;
import com.mediconnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestParam String email, HttpServletRequest request) {
        User authenticatedUser = currentUser(request);
        if (!authenticatedUser.getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own profile");
        }
        return ResponseEntity.ok(authenticatedUser);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user, HttpServletRequest request) {
        user.setEmail(currentUser(request).getEmail());
        User updated = userService.updateProfile(user);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id, HttpServletRequest request) {
        User authenticatedUser = currentUser(request);
        if (!authenticatedUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own profile");
        }
        return ResponseEntity.ok(authenticatedUser);
    }

    private User currentUser(HttpServletRequest request) {
        return (User) request.getAttribute(AuthenticatedUserInterceptor.USER_ATTRIBUTE);
    }
}
