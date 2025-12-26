package com.vertdrop_v2.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.entity.AuthProvider;
import com.vertdrop_v2.entity.Role;
import com.vertdrop_v2.entity.User;
import com.vertdrop_v2.repository.RoleRepository;
import com.vertdrop_v2.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("🎉 OAuth2 authentication successful!");

        // 1. Extract OAuth2 User details
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = authToken.getPrincipal();
        String registrationId = authToken.getAuthorizedClientRegistrationId(); // "google", "facebook"

        // 2. Extract user info
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String providerId = attributes.get("sub") != null ? attributes.get("sub").toString() : attributes.get("id").toString();

        // Extract names carefully
        String firstName = null;
        String lastName = null;

        if (attributes.get("given_name") != null) {
            firstName = (String) attributes.get("given_name");
            lastName = (String) attributes.get("family_name");
        } else if (attributes.get("name") != null) {
            String name = (String) attributes.get("name");
            String[] parts = name.split(" ");
            firstName = parts[0];
            if (parts.length > 1) lastName = parts[parts.length - 1];
        }

        logger.info("👤 Processing user: {} (Provider: {})", email, registrationId);

        // 3. Sync with Database (Find or Create)
        User user = syncUserInDatabase(email, providerId, registrationId, firstName, lastName);

        // 4. Generate JWT
        logger.info("✅ Generating JWT for user: {}", user.getEmail());
        String token = jwtService.generateToken(user);
        long expiresAt = System.currentTimeMillis() + jwtService.getExpirationTime();

        // 5. Build Response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("tokenType", "Bearer");
        responseBody.put("username", user.getUsername());
        responseBody.put("email", user.getEmail());
        responseBody.put("fullName", user.getFullName());
        responseBody.put("provider", user.getProvider().toString());
        responseBody.put("roles", user.getRoles().stream().map(Role::getName).toList());
        responseBody.put("expiresAt", expiresAt);

        // 6. Send JSON Response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }

    private User syncUserInDatabase(String email, String providerId, String registrationId, String firstName, String lastName) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            // Update existing user
            User user = existingUser.get();
            if (user.getProvider() != AuthProvider.LOCAL) {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setProviderId(providerId);
                return userRepository.save(user);
            }
            return user;
        } else {
            // Create new user
            User newUser = new User();
            newUser.setUsername(email);
            newUser.setEmail(email);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setProviderId(providerId);
            newUser.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            newUser.setEnabled(true);

            // Assign default role
            Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("ROLE_CLIENT");
                        return roleRepository.save(r);
                    });

            Set<Role> roles = new HashSet<>();
            roles.add(clientRole);
            newUser.setRoles(roles);

            return userRepository.save(newUser);
        }
    }
}