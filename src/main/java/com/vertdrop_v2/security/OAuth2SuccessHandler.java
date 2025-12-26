package com.vertdrop_v2.security;

import com.fasterxml.jackson.databind. ObjectMapper;
import com.vertdrop_v2.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("🎉 OAuth2 authentication successful!");

        // Get the authenticated user
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        logger.info("✅ Generating JWT for OAuth2 user: {} (provider: {})", user.getEmail(), user.getProvider());

        // Generate JWT token using JwtService
        String token = jwtService.generateToken(user);

        // Calculate expiration timestamp
        long expiresAt = System.currentTimeMillis() + jwtService.getExpirationTime();

        // Create response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("tokenType", "Bearer");
        responseBody.put("username", user. getUsername());
        responseBody.put("email", user.getEmail());
        responseBody.put("fullName", user.getFullName());
        responseBody.put("provider", user.getProvider().toString());
        responseBody.put("roles", user.getRoles().stream()
                .map(role -> role.getName())
                .toList());
        responseBody.put("expiresAt", expiresAt);

        // Return JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));

        logger.info("🔑 JWT token generated and returned for user: {} (email: {})", user.getUsername(), user.getEmail());
    }
}