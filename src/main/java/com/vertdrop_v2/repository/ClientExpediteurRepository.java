package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.ClientExpediteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientExpediteurRepository extends JpaRepository<ClientExpediteur, Long> {

    List<ClientExpediteur> findByNomContainingIgnoreCase(String nom);
}