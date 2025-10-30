package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.mapper.LivreurMapper;
import com.vertdrop_v2.repository.LivreurRepository;
import com.vertdrop_v2.service.LivreurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LivreurServiceImpl implements LivreurService {

    private final LivreurRepository livreurRepository;
    private final LivreurMapper livreurMapper;

    public LivreurServiceImpl(LivreurRepository livreurRepository, LivreurMapper livreurMapper) {
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
    }

    @Override
    public LivreurDTO save(LivreurDTO livreurDTO) {
        Livreur entity = livreurMapper.toEntity(livreurDTO);
        Livreur savedEntity = livreurRepository.save(entity);
        return livreurMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LivreurDTO> findById(Long id) {
        return livreurRepository.findById(id).map(livreurMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LivreurDTO> findAll() {
        return livreurRepository.findAll().stream()
                .map(livreurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        livreurRepository.deleteById(id);
    }
}