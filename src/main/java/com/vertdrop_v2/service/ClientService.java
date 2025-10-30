package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ClientExpediteurDTO;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    ClientExpediteurDTO save(ClientExpediteurDTO clientDTO);

    Optional<ClientExpediteurDTO> findById(Long id);

    List<ClientExpediteurDTO> findAll();

    void deleteById(Long id);
}