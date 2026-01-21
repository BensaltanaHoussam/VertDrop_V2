package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.PermissionDTO;
import com.vertdrop_v2.dto.RoleWithPermissionsDTO;

import java.util.Set;

public interface RolePermissionService {
    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);

    Set<PermissionDTO> getPermissionsByRoleId(Long roleId);

    RoleWithPermissionsDTO getRoleWithPermissions(Long roleId);
}