package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ColisDTO;

import java.util.List;
import java.util.Optional;

public interface ColisService {

    ColisDTO save(ColisDTO colisDTO);

    Optional<ColisDTO> findById(Long id);

    List<ColisDTO> findAll();

    void deleteById(Long id);
}