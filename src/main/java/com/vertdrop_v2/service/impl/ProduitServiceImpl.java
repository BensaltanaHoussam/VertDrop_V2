package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.entity.Produit;
import com.vertdrop_v2.mapper.ProduitMapper;
import com.vertdrop_v2.repository.ProduitRepository;
import com.vertdrop_v2.service.ProduitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    public ProduitServiceImpl(ProduitRepository produitRepository, ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
    }

    @Override
    public ProduitDTO save(ProduitDTO produitDTO) {
        Produit entity = produitMapper.toEntity(produitDTO);
        Produit savedEntity = produitRepository.save(entity);
        return produitMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProduitDTO> findById(Long id) {
        return produitRepository.findById(id).map(produitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitDTO> findAll() {
        return produitRepository.findAll().stream()
                .map(produitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitDTO> findByClientId(Long clientId) {
        return produitRepository.findByClientExpediteurId(clientId).stream()
                .map(produitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        produitRepository.deleteById(id);
    }
}