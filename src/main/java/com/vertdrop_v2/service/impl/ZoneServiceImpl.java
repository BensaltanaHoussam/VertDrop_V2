package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Zone;
import com.vertdrop_v2.mapper.ZoneMapper;
import com.vertdrop_v2.repository.ZoneRepository;
import com.vertdrop_v2.service.ZoneService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    public ZoneServiceImpl(ZoneRepository zoneRepository, ZoneMapper zoneMapper) {
        this.zoneRepository = zoneRepository;
        this.zoneMapper = zoneMapper;
    }

    @Override
    public ZoneDTO save(ZoneDTO zoneDTO) {
        Zone entity = zoneMapper.toEntity(zoneDTO);
        Zone savedEntity = zoneRepository.save(entity);
        return zoneMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ZoneDTO> findById(Long id) {
        return zoneRepository.findById(id).map(zoneMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneDTO> findAll() {
        return zoneRepository.findAll().stream()
                .map(zoneMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        zoneRepository.deleteById(id);
    }
}