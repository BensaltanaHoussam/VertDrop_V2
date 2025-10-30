package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.LivreurDTO;

import java.util.List;
import java.util.Optional;

public interface LivreurService {

    LivreurDTO save(LivreurDTO livreurDTO);

    Optional<LivreurDTO> findById(Long id);

    List<LivreurDTO> findAll();

    void deleteById(Long id);
}