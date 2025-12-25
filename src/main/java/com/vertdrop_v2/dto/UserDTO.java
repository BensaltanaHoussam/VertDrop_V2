package com.vertdrop_v2.dto;

import com.vertdrop_v2.entity.AuthProvider;
import lombok.AllArgsConstructor;
import lombok. Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private AuthProvider provider;
    private String providerId;
    private Set<String> roles;
    private Boolean enabled;

    /**
     * Get full name (firstName + lastName)
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }

    /**
     * Check if OAuth2 user
     */
    public boolean isOAuth2User() {
        return provider != null && provider != AuthProvider.LOCAL;
    }
}