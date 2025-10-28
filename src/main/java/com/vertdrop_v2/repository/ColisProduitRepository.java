package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.ColisProduit;
import com.vertdrop_v2.entity.ColisProduitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColisProduitRepository extends JpaRepository<ColisProduit, ColisProduitId> {
}

