package com.vertdrop_v2.service.impl;

import com.vertdrop_v2.dto.PermissionDTO;
import com.vertdrop_v2.entity.Permission;
import com.vertdrop_v2.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl service;

    @Test
    void createPermission_whenAlreadyExists_thenThrow() {
        PermissionDTO dto = new PermissionDTO(null, "READ", "desc");
        when(permissionRepository.existsByName("READ")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createPermission(dto));
        assertTrue(ex.getMessage().contains("READ"));

        verify(permissionRepository).existsByName("READ");
        verifyNoMoreInteractions(permissionRepository);
    }

    @Test
    void createPermission_whenNotExists_thenSaveAndReturnDTO() {
        PermissionDTO dto = new PermissionDTO(null, "WRITE", "write desc");
        when(permissionRepository.existsByName("WRITE")).thenReturn(false);

        Permission saved = new Permission();
        saved.setId(42L);
        saved.setName("WRITE");
        saved.setDescription("write desc");
        when(permissionRepository.save(any(Permission.class))).thenReturn(saved);

        PermissionDTO result = service.createPermission(dto);

        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals("WRITE", result.getName());
        assertEquals("write desc", result.getDescription());

        verify(permissionRepository).existsByName("WRITE");
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    void findById_whenFound_thenReturnDTO() {
        Permission p = new Permission();
        p.setId(5L);
        p.setName("EXEC");
        p.setDescription("exec desc");
        when(permissionRepository.findById(5L)).thenReturn(Optional.of(p));

        Optional<PermissionDTO> opt = service.findById(5L);

        assertTrue(opt.isPresent());
        PermissionDTO dto = opt.get();
        assertEquals(5L, dto.getId());
        assertEquals("EXEC", dto.getName());

        verify(permissionRepository).findById(5L);
    }

    @Test
    void findById_whenNotFound_thenEmpty() {
        when(permissionRepository.findById(10L)).thenReturn(Optional.empty());

        Optional<PermissionDTO> opt = service.findById(10L);

        assertFalse(opt.isPresent());
        verify(permissionRepository).findById(10L);
    }

    @Test
    void findAll_returnsDTOList() {
        Permission p1 = new Permission();
        p1.setId(1L);
        p1.setName("A");
        p1.setDescription("a");
        Permission p2 = new Permission();
        p2.setId(2L);
        p2.setName("B");
        p2.setDescription("b");

        when(permissionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<PermissionDTO> list = service.findAll();

        assertEquals(2, list.size());
        assertEquals("A", list.get(0).getName());
        assertEquals("B", list.get(1).getName());

        verify(permissionRepository).findAll();
    }

    @Test
    void findAll_whenEmpty_thenReturnEmptyList() {
        when(permissionRepository.findAll()).thenReturn(Collections.emptyList());

        List<PermissionDTO> list = service.findAll();

        assertNotNull(list);
        assertTrue(list.isEmpty());

        verify(permissionRepository).findAll();
    }

    @Test
    void deleteById_delegatesToRepository() {
        doNothing().when(permissionRepository).deleteById(7L);

        service.deleteById(7L);

        verify(permissionRepository).deleteById(7L);
    }
}
