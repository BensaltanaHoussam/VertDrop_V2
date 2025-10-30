package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.DestinataireDTO;

import java.util.List;
import java.util.Optional;

public interface DestinataireService {

    DestinataireDTO save(DestinataireDTO destinataireDTO);

    Optional<DestinataireDTO> findById(Long id);

    List<DestinataireDTO> findAll();

    void deleteById(Long id);
}