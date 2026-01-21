package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.mapper.ClientExpediteurMapper;
import com.vertdrop_v2.repository.ClientExpediteurRepository;
import com.vertdrop_v2.repository.RoleRepository;
import com.vertdrop_v2.repository.UserRepository;
import com.vertdrop_v2.service.ClientService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientExpediteurRepository clientRepository,
            ClientExpediteurMapper clientMapper,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientExpediteurDTO save(ClientExpediteurDTO clientDTO) {
        ClientExpediteur entity;
        if (clientDTO.getId() != null) {
            entity = clientRepository.findById(clientDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            entity.setNom(clientDTO.getNom());
            entity.setPrenom(clientDTO.getPrenom());
            entity.setTelephone(clientDTO.getTelephone());
            entity.setAdresse(clientDTO.getAdresse());
            entity.setEmail(clientDTO.getEmail());

            if (entity.getUser() != null) {
                entity.getUser().setEmail(clientDTO.getEmail());
                entity.getUser().setUsername(clientDTO.getEmail());
                if (clientDTO.getPassword() != null && !clientDTO.getPassword().isEmpty()) {
                    entity.getUser().setPassword(passwordEncoder.encode(clientDTO.getPassword()));
                }
            }
        } else {
            entity = clientMapper.toEntity(clientDTO);
            com.vertdrop_v2.entity.User user = new com.vertdrop_v2.entity.User();
            user.setEmail(clientDTO.getEmail());
            user.setUsername(clientDTO.getEmail());
            user.setFirstName(clientDTO.getPrenom());
            user.setLastName(clientDTO.getNom());
            String pwd = clientDTO.getPassword() != null ? clientDTO.getPassword() : "Client123";
            user.setPassword(passwordEncoder.encode(pwd));
            user.setEnabled(true);

            com.vertdrop_v2.entity.Role role = roleRepository.findByName("ROLE_CLIENT")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_CLIENT not found"));
            user.getRoles().add(role);

            com.vertdrop_v2.entity.User savedUser = userRepository.save(user);
            entity.setUser(savedUser);
        }

        ClientExpediteur savedEntity = clientRepository.save(entity);
        ClientExpediteurDTO result = clientMapper.toDto(savedEntity);
        result.setEmail(savedEntity.getUser() != null ? savedEntity.getUser().getEmail() : savedEntity.getEmail());
        return result;
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