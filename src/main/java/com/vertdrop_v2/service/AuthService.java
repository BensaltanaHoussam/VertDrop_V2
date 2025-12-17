package com.vertdrop_v2.service;

import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.entity.User;
import com.vertdrop_v2.repository.ClientExpediteurRepository;
import com.vertdrop_v2.repository.LivreurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ClientExpediteurRepository clientExpediteurRepository;
    private final LivreurRepository livreurRepository;

    public AuthService(ClientExpediteurRepository clientExpediteurRepository,
                       LivreurRepository livreurRepository) {
        this.clientExpediteurRepository = clientExpediteurRepository;
        this.livreurRepository = livreurRepository;
    }

    /**
     * Get the currently authenticated User
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    /**
     * Get the ClientExpediteur linked to the current user (if exists)
     */
    public Optional<ClientExpediteur> getCurrentClient() {
        User user = getCurrentUser();
        if (user == null) {
            return Optional. empty();
        }
        return clientExpediteurRepository.findByUser(user);
    }

    /**
     * Get the Livreur linked to the current user (if exists)
     */
    public Optional<Livreur> getCurrentLivreur() {
        User user = getCurrentUser();
        if (user == null) {
            return Optional.empty();
        }
        return livreurRepository.findByUser(user);
    }

    /**
     * Check if current user has a specific role
     */
    public boolean hasRole(String roleName) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleName));
    }
}