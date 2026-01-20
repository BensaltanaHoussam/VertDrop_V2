package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.auth.LoginRequest;
import com.vertdrop_v2.dto.auth.LoginResponse;
import com.vertdrop_v2.entity.User;
import com.vertdrop_v2.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints for classic login and OAuth2")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("🔐 Login attempt for user: {}", request.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtService.generateToken(userDetails);

            // Calculate expiration time
            long expiresAt = System.currentTimeMillis() + jwtService.getExpirationTime();

            logger.info("✅ Login successful for user: {}", request.getUsername());

            // Return response
            LoginResponse response = new LoginResponse(token, "Bearer", expiresAt);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            logger.warn("❌ Invalid credentials for user: {}", request.getUsername());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "Invalid username or password");
            error.put("status", "401");

            return ResponseEntity.status(401).body(error);

        } catch (Exception ex) {
            logger.error("❌ Authentication error for user {}: {}", request.getUsername(), ex.getMessage());

            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication Failed");
            error.put("message", "An error occurred during authentication");
            error.put("status", "500");

            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            logger.warn("⚠️ Unauthorized access attempt to /auth/me");

            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "No valid authentication found");
            error.put("status", "401");

            return ResponseEntity.status(401).body(error);
        }

        logger.info("📋 User info requested for:  {}", userDetails.getUsername());

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());

        // If UserDetails is actually our User entity
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("fullName", user.getFullName());
            response.put("provider", user.getProvider().toString());
            response.put("isOAuth2User", user.isOAuth2User());
            response.put("roles", user.getRoles().stream()
                    .map(role -> role.getName())
                    .toList());
        } else {
            // Fallback for generic UserDetails
            response.put("roles", userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .toList());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Auth service health check", description = "Check if authentication service is running")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", new Date().toString());
        return ResponseEntity.ok(response);
    }
}