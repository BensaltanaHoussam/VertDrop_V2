package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ProduitDTO;

import java.util.List;
import java.util.Optional;

public interface ProduitService {

    ProduitDTO save(ProduitDTO produitDTO);

    Optional<ProduitDTO> findById(Long id);

    List<ProduitDTO> findAll();

    List<ProduitDTO> findByClientId(Long clientId);

    void deleteById(Long id);
}