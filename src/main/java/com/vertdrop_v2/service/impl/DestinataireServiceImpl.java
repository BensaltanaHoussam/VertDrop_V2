package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.entity.Destinataire;
import com.vertdrop_v2.mapper.DestinataireMapper;
import com.vertdrop_v2.repository.DestinataireRepository;
import com.vertdrop_v2.service.DestinataireService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DestinataireServiceImpl implements DestinataireService {

    private final DestinataireRepository destinataireRepository;
    private final DestinataireMapper destinataireMapper;

    public DestinataireServiceImpl(DestinataireRepository destinataireRepository, DestinataireMapper destinataireMapper) {
        this.destinataireRepository = destinataireRepository;
        this.destinataireMapper = destinataireMapper;
    }

    @Override
    public DestinataireDTO save(DestinataireDTO destinataireDTO) {
        Destinataire entity = destinataireMapper.toEntity(destinataireDTO);
        Destinataire savedEntity = destinataireRepository.save(entity);
        return destinataireMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DestinataireDTO> findById(Long id) {
        return destinataireRepository.findById(id).map(destinataireMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinataireDTO> findAll() {
        return destinataireRepository.findAll().stream()
                .map(destinataireMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        destinataireRepository.deleteById(id);
    }
}