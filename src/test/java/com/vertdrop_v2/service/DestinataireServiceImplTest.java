package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.entity.Destinataire;
import com.vertdrop_v2.mapper.DestinataireMapper;
import com.vertdrop_v2.repository.DestinataireRepository;
import com.vertdrop_v2.service.impl.DestinataireServiceImpl;
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
class DestinataireServiceImplTest {

    @Mock
    private DestinataireRepository destinataireRepository;

    @Mock
    private DestinataireMapper destinataireMapper;

    @InjectMocks
    private DestinataireServiceImpl destinataireService;

    private Destinataire entity;
    private DestinataireDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Destinataire();
        entity.setId(1L);

        dto = new DestinataireDTO();
        dto.setId(1L);
    }

    @Test
    void whenSave_shouldReturnSavedDTO() {
        when(destinataireMapper.toEntity(any(DestinataireDTO.class))).thenReturn(entity);
        when(destinataireRepository.save(any(Destinataire.class))).thenReturn(entity);
        when(destinataireMapper.toDto(any(Destinataire.class))).thenReturn(dto);

        DestinataireDTO saved = destinataireService.save(new DestinataireDTO());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        verify(destinataireMapper).toEntity(any(DestinataireDTO.class));
        verify(destinataireRepository).save(any(Destinataire.class));
        verify(destinataireMapper).toDto(any(Destinataire.class));
    }

    @Test
    void whenFindById_existing_shouldReturnDTO() {
        when(destinataireRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(destinataireMapper.toDto(entity)).thenReturn(dto);

        Optional<DestinataireDTO> found = destinataireService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        verify(destinataireRepository).findById(1L);
        verify(destinataireMapper).toDto(entity);
    }

    @Test
    void whenFindById_nonExisting_shouldReturnEmpty() {
        when(destinataireRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<DestinataireDTO> found = destinataireService.findById(99L);

        assertThat(found).isNotPresent();
        verify(destinataireRepository).findById(99L);
        verifyNoInteractions(destinataireMapper);
    }

    @Test
    void whenFindAll_shouldReturnListOfDTOs() {
        when(destinataireRepository.findAll()).thenReturn(Collections.singletonList(entity));
        when(destinataireMapper.toDto(entity)).thenReturn(dto);

        List<DestinataireDTO> list = destinataireService.findAll();

        assertThat(list).isNotNull().hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(1L);
        verify(destinataireRepository).findAll();
        verify(destinataireMapper).toDto(entity);
    }

    @Test
    void whenDeleteById_shouldInvokeRepository() {
        Long id = 1L;
        doNothing().when(destinataireRepository).deleteById(id);

        destinataireService.deleteById(id);

        verify(destinataireRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(destinataireRepository);
    }
}
