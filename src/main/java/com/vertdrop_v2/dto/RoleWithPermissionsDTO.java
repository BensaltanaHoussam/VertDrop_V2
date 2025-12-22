package com.vertdrop_v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleWithPermissionsDTO {
    private Long id;
    private String name;
    private Set<PermissionDTO> permissions;
}