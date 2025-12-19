package com.vertdrop_v2.controller;

import com.vertdrop_v2.dto.PermissionDTO;
import com.vertdrop_v2.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@PreAuthorize("hasRole('MANAGER')")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        return permissionService.findById(id)
                .map(ResponseEntity:: ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PermissionDTO> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO created = permissionService.createPermission(permissionDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}