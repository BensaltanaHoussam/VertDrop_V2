package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.mapper.ClientExpediteurMapper;
import com.vertdrop_v2.repository.ClientExpediteurRepository;
import com.vertdrop_v2.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientExpediteurRepository clientRepository;
    private final ClientExpediteurMapper clientMapper;

    public ClientServiceImpl(ClientExpediteurRepository clientRepository, ClientExpediteurMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public ClientExpediteurDTO save(ClientExpediteurDTO clientDTO) {
        ClientExpediteur entity = clientMapper.toEntity(clientDTO);
        ClientExpediteur savedEntity = clientRepository.save(entity);
        return clientMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientExpediteurDTO> findById(Long id) {
        return clientRepository.findById(id).map(clientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientExpediteurDTO> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }
}