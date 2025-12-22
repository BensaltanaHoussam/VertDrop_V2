package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.PermissionDTO;
import com.vertdrop_v2.dto.RoleWithPermissionsDTO;
import com.vertdrop_v2.entity.Permission;
import com. vertdrop_v2.entity.Role;
import com.vertdrop_v2.repository. PermissionRepository;
import com.vertdrop_v2.repository.RoleRepository;
import com.vertdrop_v2.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util. Set;
import java.util.stream. Collectors;

@Service
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this. permissionRepository = permissionRepository;
    }

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found:  " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));

        role.getPermissions().remove(permission);
        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PermissionDTO> getPermissionsByRoleId(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

        return role.getPermissions().stream()
                .map(p -> new PermissionDTO(p.getId(), p.getName(), p.getDescription()))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleWithPermissionsDTO getRoleWithPermissions(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));

        Set<PermissionDTO> permissions = role. getPermissions().stream()
                .map(p -> new PermissionDTO(p.getId(), p.getName(), p.getDescription()))
                .collect(Collectors. toSet());

        return new RoleWithPermissionsDTO(role.getId(), role.getName(), permissions);
    }
}