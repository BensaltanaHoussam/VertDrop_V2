package com.vertdrop_v2.repository;

import com. vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.entity.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain. Pageable;
import org.springframework.data.jpa.repository. JpaRepository;
import org. springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query. Param;
import org.springframework. stereotype.Repository;

import java. math.BigDecimal;
import java.util.List;

@Repository
public interface ColisRepository extends JpaRepository<Colis, Long> {
    List<Colis> findByStatut(StatutColis statut);
    List<Colis> findByLivreurId(Long livreurId);

    @Query("SELECT COALESCE(SUM(c.poids), 0) FROM Colis c WHERE c.zone.id = :zoneId")
    BigDecimal sumPoidsByZone(@Param("zoneId") Long zoneId);

    @Query("""
    SELECT c FROM Colis c
    WHERE (:statut IS NULL OR c.statut = :statut)
      AND (:zoneId IS NULL OR c.zone.id = :zoneId)
      AND (
            :keyword IS NULL OR (
                c.description LIKE %:keyword%
                OR c.clientExpediteur. nom LIKE %:keyword%
                OR c.clientExpediteur.prenom LIKE %: keyword%
                OR c.destinataire.nom LIKE %: keyword%
                OR c.destinataire.prenom LIKE %:keyword%
            )
          )
    """)
    Page<Colis> findWithFilters(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            @Param("keyword") String keyword,
            Pageable pageable);


    List<Colis> findByClientExpediteurId(Long clientId);

    @Query("""
    SELECT c FROM Colis c
    WHERE c.livreur.id = : livreurId
      AND (:statut IS NULL OR c. statut = :statut)
      AND (:zoneId IS NULL OR c.zone.id = :zoneId)
      AND (
            :keyword IS NULL OR (
                c.description LIKE %:keyword%
                OR c.clientExpediteur.nom LIKE %:keyword%
                OR c.clientExpediteur.prenom LIKE %:keyword%
                OR c.destinataire.nom LIKE %:keyword%
                OR c.destinataire.prenom LIKE %: keyword%
            )
          )
    """)
    Page<Colis> findWithFiltersAndLivreur(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            @Param("keyword") String keyword,
            @Param("livreurId") Long livreurId,
            Pageable pageable);


    @Query("""
    SELECT c FROM Colis c
    WHERE c.clientExpediteur.id = :clientId
      AND (:statut IS NULL OR c. statut = :statut)
      AND (:zoneId IS NULL OR c.zone.id = :zoneId)
      AND (
            :keyword IS NULL OR (
                c.description LIKE %:keyword%
                OR c.destinataire.nom LIKE %: keyword%
                OR c.destinataire.prenom LIKE %:keyword%
            )
          )
    """)
    Page<Colis> findWithFiltersAndClient(
            @Param("statut") StatutColis statut,
            @Param("zoneId") Long zoneId,
            @Param("keyword") String keyword,
            @Param("clientId") Long clientId,
            Pageable pageable);
}