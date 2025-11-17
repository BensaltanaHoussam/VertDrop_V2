// language: java
package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.Livreur;
import com.vertdrop_v2.mapper.LivreurMapper;
import com.vertdrop_v2.repository.LivreurRepository;
import com.vertdrop_v2.service.impl.LivreurServiceImpl;
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
class LivreurServiceImplTest {

    @Mock
    private LivreurRepository livreurRepository;

    @Mock
    private LivreurMapper livreurMapper;

    @InjectMocks
    private LivreurServiceImpl livreurService;

    private Livreur entity;
    private LivreurDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Livreur();
        entity.setId(1L);

        dto = new LivreurDTO();
        dto.setId(1L);
    }

    @Test
    void whenSave_shouldReturnSavedDTO() {
        when(livreurMapper.toEntity(any(LivreurDTO.class))).thenReturn(entity);
        when(livreurRepository.save(any(Livreur.class))).thenReturn(entity);
        when(livreurMapper.toDto(any(Livreur.class))).thenReturn(dto);

        LivreurDTO saved = livreurService.save(new LivreurDTO());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        verify(livreurMapper).toEntity(any(LivreurDTO.class));
        verify(livreurRepository).save(any(Livreur.class));
        verify(livreurMapper).toDto(any(Livreur.class));
    }

    @Test
    void whenFindById_existing_shouldReturnDTO() {
        when(livreurRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(livreurMapper.toDto(entity)).thenReturn(dto);

        Optional<LivreurDTO> found = livreurService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        verify(livreurRepository).findById(1L);
        verify(livreurMapper).toDto(entity);
    }

    @Test
    void whenFindById_nonExisting_shouldReturnEmpty() {
        when(livreurRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<LivreurDTO> found = livreurService.findById(99L);

        assertThat(found).isNotPresent();
        verify(livreurRepository).findById(99L);
        verifyNoInteractions(livreurMapper);
    }

    @Test
    void whenFindAll_shouldReturnListOfDTOs() {
        when(livreurRepository.findAll()).thenReturn(Collections.singletonList(entity));
        when(livreurMapper.toDto(entity)).thenReturn(dto);

        List<LivreurDTO> list = livreurService.findAll();

        assertThat(list).isNotNull().hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(1L);
        verify(livreurRepository).findAll();
        verify(livreurMapper).toDto(entity);
    }

    @Test
    void whenDeleteById_shouldInvokeRepository() {
        Long id = 1L;
        doNothing().when(livreurRepository).deleteById(id);

        livreurService.deleteById(id);

        verify(livreurRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(livreurRepository);
    }
}
