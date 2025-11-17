package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.entity.Zone;
import com.vertdrop_v2.mapper.ZoneMapper;
import com.vertdrop_v2.repository.ZoneRepository;
import com.vertdrop_v2.service.impl.ZoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZoneServiceImpl zoneService;

    private Zone entity;
    private ZoneDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Zone();
        entity.setId(1L);

        dto = new ZoneDTO();
        dto.setId(1L);
    }

    @Test
    void whenSave_shouldReturnSavedDTO() {
        when(zoneMapper.toEntity(any(ZoneDTO.class))).thenReturn(entity);
        when(zoneRepository.save(any(Zone.class))).thenReturn(entity);
        when(zoneMapper.toDto(any(Zone.class))).thenReturn(dto);

        ZoneDTO saved = zoneService.save(new ZoneDTO());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        verify(zoneMapper).toEntity(any(ZoneDTO.class));
        verify(zoneRepository).save(any(Zone.class));
        verify(zoneMapper).toDto(any(Zone.class));
    }

    @Test
    void whenFindById_existing_shouldReturnDTO() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(zoneMapper.toDto(entity)).thenReturn(dto);

        Optional<ZoneDTO> found = zoneService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        verify(zoneRepository).findById(1L);
        verify(zoneMapper).toDto(entity);
    }

    @Test
    void whenFindById_nonExisting_shouldReturnEmpty() {
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ZoneDTO> found = zoneService.findById(99L);

        assertThat(found).isNotPresent();
        verify(zoneRepository).findById(99L);
        verifyNoInteractions(zoneMapper);
    }

    @Test
    void whenFindAll_shouldReturnListOfDTOs() {
        when(zoneRepository.findAll()).thenReturn(Collections.singletonList(entity));
        when(zoneMapper.toDto(entity)).thenReturn(dto);

        List<ZoneDTO> list = zoneService.findAll();

        assertThat(list).isNotNull().hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(1L);
        verify(zoneRepository).findAll();
        verify(zoneMapper).toDto(entity);
    }

    @Test
    void whenDeleteById_shouldInvokeRepository() {
        Long id = 1L;
        doNothing().when(zoneRepository).deleteById(id);

        zoneService.deleteById(id);

        verify(zoneRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(zoneRepository);
    }
}
