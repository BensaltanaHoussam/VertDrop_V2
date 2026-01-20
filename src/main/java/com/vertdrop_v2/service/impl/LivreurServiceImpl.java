package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.mapper.LivreurMapper;
import com.vertdrop_v2.repository.LivreurRepository;
import com.vertdrop_v2.repository.RoleRepository;
import com.vertdrop_v2.repository.UserRepository;
import com.vertdrop_v2.service.LivreurService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LivreurServiceImpl(LivreurRepository livreurRepository,
            LivreurMapper livreurMapper,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LivreurDTO save(LivreurDTO livreurDTO) {
        Livreur entity;
        if (livreurDTO.getId() != null) {
            entity = livreurRepository.findById(livreurDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Livreur not found"));
            entity.setNom(livreurDTO.getNom());
            entity.setPrenom(livreurDTO.getPrenom());
            entity.setTelephone(livreurDTO.getTelephone());
            entity.setVehicule(livreurDTO.getVehicule());
            // zoneAssignee handling might be needed here if not handled by mapper

            if (entity.getUser() != null) {
                entity.getUser().setEmail(livreurDTO.getEmail());
                entity.getUser().setUsername(livreurDTO.getEmail());
                if (livreurDTO.getPassword() != null && !livreurDTO.getPassword().isEmpty()) {
                    entity.getUser().setPassword(passwordEncoder.encode(livreurDTO.getPassword()));
                }
            }
        } else {
            entity = livreurMapper.toEntity(livreurDTO);
            com.vertdrop_v2.entity.User user = new com.vertdrop_v2.entity.User();
            user.setEmail(livreurDTO.getEmail());
            user.setUsername(livreurDTO.getEmail()); // Using email as username
            user.setFirstName(livreurDTO.getPrenom());
            user.setLastName(livreurDTO.getNom());
            String pwd = livreurDTO.getPassword() != null ? livreurDTO.getPassword() : "Livreur123";
            user.setPassword(passwordEncoder.encode(pwd));
            user.setEnabled(true);

            com.vertdrop_v2.entity.Role role = roleRepository.findByName("ROLE_LIVREUR")
                    .orElseThrow(() -> new RuntimeException("Role ROLE_LIVREUR not found"));
            user.getRoles().add(role);

            com.vertdrop_v2.entity.User savedUser = userRepository.save(user);
            entity.setUser(savedUser);
        }

        Livreur savedEntity = livreurRepository.save(entity);
        LivreurDTO result = livreurMapper.toDto(savedEntity);
        result.setEmail(savedEntity.getUser() != null ? savedEntity.getUser().getEmail() : null);
        return result;
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