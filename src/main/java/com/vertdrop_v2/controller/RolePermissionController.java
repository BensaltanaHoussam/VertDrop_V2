package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.PermissionDTO;
import com.vertdrop_v2.dto.RoleWithPermissionsDTO;
import com.vertdrop_v2.service.RolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('MANAGER')")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<Set<PermissionDTO>> getRolePermissions(@PathVariable Long roleId) {
        return ResponseEntity.ok(rolePermissionService.getPermissionsByRoleId(roleId));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleWithPermissionsDTO> getRoleWithPermissions(@PathVariable Long roleId) {
        return ResponseEntity.ok(rolePermissionService.getRoleWithPermissions(roleId));
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }
}