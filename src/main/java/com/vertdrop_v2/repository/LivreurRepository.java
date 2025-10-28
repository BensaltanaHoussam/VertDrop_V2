package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LivreurRepository extends JpaRepository<Livreur, Long> {

    List<Livreur> findByNomContainingIgnoreCase(String nom);
}
