package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.PermissionDTO;
import com.vertdrop_v2.entity.Permission;
import com.vertdrop_v2.repository.PermissionRepository;
import com.vertdrop_v2.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        if (permissionRepository.existsByName(permissionDTO.getName())) {
            throw new RuntimeException("Permission already exists:  " + permissionDTO.getName());
        }

        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());

        Permission saved = permissionRepository.save(permission);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PermissionDTO> findById(Long id) {
        return permissionRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    private PermissionDTO toDTO(Permission permission) {
        return new PermissionDTO(
                permission.getId(),
                permission.getName(),
                permission.getDescription());
    }
}