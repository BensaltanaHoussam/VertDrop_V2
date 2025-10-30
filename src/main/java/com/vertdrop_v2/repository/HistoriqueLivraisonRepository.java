package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.HistoriqueLivraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoriqueLivraisonRepository extends JpaRepository<HistoriqueLivraison, Long> {

    List<HistoriqueLivraison> findByColisIdOrderByDateChangementDesc(Long colisId);
}
