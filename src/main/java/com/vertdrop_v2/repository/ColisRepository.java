package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface ColisRepository extends JpaRepository<Colis, Long> {

    List<Colis> findByStatut(StatutColis statut);

    List<Colis> findByLivreur(Livreur livreur);

    List<Colis> findByClientExpediteur(ClientExpediteur clientExpediteur);

    List<Colis> findByZone(Zone zone);


    List<Colis> findByClientExpediteurAndStatutIn(ClientExpediteur client, List<StatutColis> statuts);

    Page<Colis> findByStatut(StatutColis statut, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.poids), 0) FROM Colis c WHERE c.zone.id = :zoneId")
    BigDecimal sumPoidsByZone(@Param("zoneId") Long zoneId);

    @Query("SELECT c FROM Colis c WHERE " +
            "(:statut IS NULL OR c.statut = :statut) AND " +
            "(:zoneId IS NULL OR c.zone.id = :zoneId)")
    Page<Colis> findWithFilters(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            Pageable pageable);


    List<Colis> findByLivreurId(Long livreurId);
}
