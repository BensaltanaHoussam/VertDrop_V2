package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.PermissionDTO;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    PermissionDTO createPermission(PermissionDTO permissionDTO);

    Optional<PermissionDTO> findById(Long id);

    List<PermissionDTO> findAll();

    void deleteById(Long id);
}