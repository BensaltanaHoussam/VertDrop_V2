package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}