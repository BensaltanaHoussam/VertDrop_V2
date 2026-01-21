package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ColisCreateRequestDTO;
import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.entity.StatutColis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ColisService {
    ColisDTO createFromRequest(ColisCreateRequestDTO req);

    ColisDTO save(ColisDTO colisDTO);

    Optional<ColisDTO> findById(Long id);

    Page<ColisDTO> findAll(Pageable pageable, StatutColis statut, Long zoneId, String keyword, Long clientId,
            Long livreurId);

    Page<ColisDTO> findAllForLivreur(Long livreurId, Pageable pageable, StatutColis statut, Long zoneId,
            String keyword);

    Page<ColisDTO> findAllForClient(Long clientId, Pageable pageable, StatutColis statut, Long zoneId, String keyword);

    void deleteById(Long id);

    ColisDTO updateStatus(Long colisId, StatutColis newStatus, String comment);

    ColisDTO assignLivreur(Long colisId, Long livreurId);

    BigDecimal calculateTotalWeightByZone(Long zoneId);

    List<ColisDTO> findByLivreurId(Long livreurId);

    List<ColisDTO> findByClientId(Long clientId);

    List<HistoriqueLivraisonDTO> findHistoryForColis(Long colisId);
}