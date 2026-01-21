package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.StatsSummaryDTO;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.repository.ClientExpediteurRepository;
import com.vertdrop_v2.repository.ColisRepository;
import com.vertdrop_v2.repository.LivreurRepository;
import com.vertdrop_v2.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final ColisRepository colisRepository;
    private final LivreurRepository livreurRepository;
    private final ClientExpediteurRepository clientRepository;

    @Override
    public StatsSummaryDTO getSummary() {
        long totalColis = colisRepository.count();
        long totalLivreurs = livreurRepository.count();
        long totalClients = clientRepository.count();

        // Sum weights
        BigDecimal totalWeight = colisRepository.findAll().stream()
                .map(c -> c.getPoids() != null ? c.getPoids() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Count by status
        Map<String, Long> countByStatus = new HashMap<>();
        for (StatutColis status : StatutColis.values()) {
            countByStatus.put(status.name(), (long) colisRepository.findByStatut(status).size());
        }

        return StatsSummaryDTO.builder()
                .totalColis(totalColis)
                .totalLivreurs(totalLivreurs)
                .totalClients(totalClients)
                .totalWeight(totalWeight)
                .countByStatus(countByStatus)
                .build();
    }
}
