package com.vertdrop_v2.security;

import com.vertdrop_v2.entity.AuthProvider;
import com.vertdrop_v2.entity.Role;
import com.vertdrop_v2.entity.User;
import com.vertdrop_v2.repository.RoleRepository;
import com.vertdrop_v2.repository. UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework. stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util. Map;
import java.util. Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Get OAuth2 user info from provider (Google)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Get provider name (google, facebook, etc.)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = getAuthProvider(registrationId);

        logger.info("🔐 OAuth2 login attempt with provider: {}", provider);

        // Process user data
        return processOAuth2User(userRequest, oAuth2User, provider);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User, AuthProvider provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Extract user info from OAuth2User attributes
        String email = extractEmail(attributes);
        String providerId = extractProviderId(attributes, provider);
        String firstName = extractFirstName(attributes);
        String lastName = extractLastName(attributes);

        logger.info("📧 Processing OAuth2 user: email={}, provider={}, providerId={}", email, provider, providerId);

        if (email == null || email.isEmpty()) {
            logger.error("❌ Email not found in OAuth2 attributes for provider: {}", provider);
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Find or create user
        User user = findOrCreateUser(email, providerId, provider, firstName, lastName);

        logger.info("✅ OAuth2 user processed:  username={}, email={}, provider={}", user.getUsername(), user.getEmail(), user.getProvider());

        // Return CustomOAuth2User with our User entity
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User findOrCreateUser(String email, String providerId, AuthProvider provider, String firstName, String lastName) {
        // Try to find by email first
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Update user info if OAuth2 login
            if (existingUser.getProvider() == AuthProvider.LOCAL) {
                logger.warn("⚠️ User {} exists with LOCAL provider, not updating to OAuth2", email);
                return existingUser;
            }

            // Update OAuth2 user info
            existingUser.setProviderId(providerId);
            existingUser.setFirstName(firstName);
            existingUser. setLastName(lastName);

            logger.info("♻️ Updating existing OAuth2 user: {}", email);
            return userRepository.save(existingUser);
        }

        // Create new user
        logger.info("➕ Creating new OAuth2 user: {}", email);
        return createNewOAuth2User(email, providerId, provider, firstName, lastName);
    }

    private User createNewOAuth2User(String email, String providerId, AuthProvider provider, String firstName, String lastName) {
        User newUser = new User();
        newUser.setUsername(email); // Use email as username
        newUser.setEmail(email);
        newUser.setProviderId(providerId);
        newUser.setProvider(provider);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPassword(null); // No password for OAuth2 users
        newUser.setEnabled(true);

        // Assign default role:  CLIENT
        Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("ROLE_CLIENT not found!  Run DataInitializer first."));

        Set<Role> roles = new HashSet<>();
        roles.add(clientRole);
        newUser.setRoles(roles);

        User savedUser = userRepository.save(newUser);
        logger.info("✅ Created new OAuth2 user: {} with role ROLE_CLIENT", savedUser.getEmail());

        return savedUser;
    }

    // Extract email from OAuth2 attributes
    private String extractEmail(Map<String, Object> attributes) {
        return (String) attributes.get("email");
    }

    // Extract provider ID (unique user ID from provider)
    private String extractProviderId(Map<String, Object> attributes, AuthProvider provider) {
        switch (provider) {
            case GOOGLE:
                return (String) attributes.get("sub"); // Google uses "sub" as user ID
            case FACEBOOK:
                return (String) attributes.get("id");
            default:
                return (String) attributes.get("id");
        }
    }

    // Extract first name
    private String extractFirstName(Map<String, Object> attributes) {
        // Try "given_name" first (Google), then "first_name" (Facebook)
        String givenName = (String) attributes.get("given_name");
        if (givenName != null) return givenName;

        String firstName = (String) attributes.get("first_name");
        if (firstName != null) return firstName;

        // Fallback:  split "name" field
        String name = (String) attributes.get("name");
        if (name != null && name.contains(" ")) {
            return name.split(" ")[0];
        }

        return name;
    }

    // Extract last name
    private String extractLastName(Map<String, Object> attributes) {
        // Try "family_name" first (Google), then "last_name" (Facebook)
        String familyName = (String) attributes.get("family_name");
        if (familyName != null) return familyName;

        String lastName = (String) attributes.get("last_name");
        if (lastName != null) return lastName;

        // Fallback: split "name" field
        String name = (String) attributes.get("name");
        if (name != null && name. contains(" ")) {
            String[] parts = name.split(" ");
            return parts[parts.length - 1];
        }

        return null;
    }

    // Convert provider string to enum
    private AuthProvider getAuthProvider(String registrationId) {
        switch (registrationId. toLowerCase()) {
            case "google":
                return AuthProvider. GOOGLE;
            case "facebook":
                return AuthProvider.FACEBOOK;
            case "apple":
                return AuthProvider. APPLE;
            default:
                return AuthProvider.LOCAL;
        }
    }
}