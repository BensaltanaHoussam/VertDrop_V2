package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.mapper.ColisMapper;
import com.vertdrop_v2.repository.ColisRepository;
import com.vertdrop_v2.service.ColisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ColisServiceImpl implements ColisService {

    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;

    public ColisServiceImpl(ColisRepository colisRepository, ColisMapper colisMapper) {
        this.colisRepository = colisRepository;
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
    public List<ColisDTO> findAll() {
        return colisRepository.findAll().stream()
                .map(colisMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        colisRepository.deleteById(id);
    }
}