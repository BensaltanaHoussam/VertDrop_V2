package com.vertdrop_v2.repository;

import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.entity.Zone;
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

    /* ===================== BASIC FINDERS ===================== */

    List<Colis> findByStatut(StatutColis statut);

    Page<Colis> findByStatut(StatutColis statut, Pageable pageable);

    List<Colis> findByLivreur(Livreur livreur);

    List<Colis> findByLivreurId(Long livreurId);

    List<Colis> findByClientExpediteur(ClientExpediteur clientExpediteur);

    List<Colis> findByClientExpediteurId(Long clientId);

    List<Colis> findByZone(Zone zone);

    List<Colis> findByClientExpediteurAndStatutIn(
            ClientExpediteur client,
            List<StatutColis> statuts
    );

    /* ===================== AGGREGATION ===================== */

    @Query("SELECT COALESCE(SUM(c.poids), 0) FROM Colis c WHERE c.zone.id = :zoneId")
    BigDecimal sumPoidsByZone(@Param("zoneId") Long zoneId);

    /* ===================== FILTERS (ADMIN / ALL) ===================== */

    @Query("""
        SELECT c FROM Colis c
        WHERE
            (:statut IS NULL OR c.statut = :statut)
        AND (:zoneId IS NULL OR c.zone.id = :zoneId)
        AND (
            :keyword = ''
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.clientExpediteur.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.clientExpediteur.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.destinataire.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.destinataire.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Colis> findWithFilters(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /* ===================== FILTERS (LIVREUR) ===================== */

    @Query("""
        SELECT c FROM Colis c
        WHERE
            c.livreur.id = :livreurId
        AND (:statut IS NULL OR c.statut = :statut)
        AND (:zoneId IS NULL OR c.zone.id = :zoneId)
        AND (
            :keyword = ''
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.clientExpediteur.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.clientExpediteur.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.destinataire.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.destinataire.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Colis> findWithFiltersAndLivreur(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            @Param("keyword") String keyword,
            @Param("livreurId") Long livreurId,
            Pageable pageable
    );

    /* ===================== FILTERS (CLIENT) ===================== */

    @Query("""
        SELECT c FROM Colis c
        WHERE
            c.clientExpediteur.id = :clientId
        AND (:statut IS NULL OR c.statut = :statut)
        AND (:zoneId IS NULL OR c.zone.id = :zoneId)
        AND (
            :keyword = ''
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.destinataire.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.destinataire.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Colis> findWithFiltersAndClient(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            @Param("keyword") String keyword,
            @Param("clientId") Long clientId,
            Pageable pageable
    );
}
