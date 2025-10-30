package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ZoneDTO;

import java.util.List;
import java.util.Optional;

public interface ZoneService {

    ZoneDTO save(ZoneDTO zoneDTO);

    Optional<ZoneDTO> findById(Long id);

    List<ZoneDTO> findAll();

    void deleteById(Long id);
}