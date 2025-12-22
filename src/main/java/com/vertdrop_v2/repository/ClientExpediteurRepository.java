package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.User;  // ← ADD this import
import org.springframework.data. jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;  // ← ADD this import

@Repository
public interface ClientExpediteurRepository extends JpaRepository<ClientExpediteur, Long> {

    List<ClientExpediteur> findByNomContainingIgnoreCase(String nom);

    Optional<ClientExpediteur> findByUser(User user);
}