package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.entity.HistoriqueLivraison;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.mapper.ColisMapper;
import com.vertdrop_v2.repository.ColisRepository;
import com.vertdrop_v2.repository.HistoriqueLivraisonRepository;
import com.vertdrop_v2.repository.LivreurRepository;
import com.vertdrop_v2.service.ColisService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColisServiceImpl implements ColisService {

    private final ColisRepository colisRepository;
    private final HistoriqueLivraisonRepository historiqueRepository;
    private final LivreurRepository livreurRepository;
    private final ColisMapper colisMapper;

    public ColisServiceImpl(ColisRepository colisRepository, HistoriqueLivraisonRepository historiqueRepository, LivreurRepository livreurRepository, ColisMapper colisMapper) {
        this.colisRepository = colisRepository;
        this.historiqueRepository = historiqueRepository;
        this.livreurRepository = livreurRepository;
        this.colisMapper = colisMapper;
    }

    @Override
    public ColisDTO save(ColisDTO colisDTO) {
        Colis entity = colisMapper.toEntity(colisDTO);
        Colis savedEntity = colisRepository.save(entity);
        return colisMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ColisDTO> findById(Long id) {
        return colisRepository.findById(id).map(colisMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findAll(Pageable pageable) {
        Page<Colis> colisPage = colisRepository.findAll(pageable);
        return colisPage.map(colisMapper::toDto);
    }

    @Override
    public void deleteById(Long id) {
        colisRepository.deleteById(id);
    }


    @Override
    public ColisDTO updateStatus(Long colisId, StatutColis newStatus, String comment) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis not found with id: " + colisId));

        colis.setStatut(newStatus);

        HistoriqueLivraison historyRecord = new HistoriqueLivraison();
        historyRecord.setColis(colis);
        historyRecord.setStatut(newStatus);
        historyRecord.setDateChangement(LocalDateTime.now());
        historyRecord.setCommentaire(comment);

        historiqueRepository.save(historyRecord);

        return colisMapper.toDto(colis);
    }


    @Override
    public ColisDTO assignLivreur(Long colisId, Long livreurId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis not found with id: " + colisId));
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new EntityNotFoundException("Livreur not found with id: " + livreurId));

        colis.setLivreur(livreur);
        return colisMapper.toDto(colis);
    }


    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalWeightByZone(Long zoneId) {

        return colisRepository.sumPoidsByZone(zoneId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColisDTO> findByLivreurId(Long livreurId) {

        List<Colis> colisList = colisRepository.findByLivreurId(livreurId);
        return colisList.stream()
                .map(colisMapper::toDto)
                .collect(Collectors.toList());


    }

}