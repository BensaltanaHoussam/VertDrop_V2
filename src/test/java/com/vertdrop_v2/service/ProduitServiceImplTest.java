package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.entity.Produit;
import com.vertdrop_v2.mapper.ProduitMapper;
import com.vertdrop_v2.repository.ProduitRepository;
import com.vertdrop_v2.service.impl.ProduitServiceImpl;
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
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private Produit entity;
    private ProduitDTO dto;

    @BeforeEach
    void setUp() {
        entity = new Produit();
        entity.setId(1L);

        dto = new ProduitDTO();
        dto.setId(1L);
    }

    @Test
    void whenSave_shouldReturnSavedDTO() {
        when(produitMapper.toEntity(any(ProduitDTO.class))).thenReturn(entity);
        when(produitRepository.save(any(Produit.class))).thenReturn(entity);
        when(produitMapper.toDto(any(Produit.class))).thenReturn(dto);

        ProduitDTO saved = produitService.save(new ProduitDTO());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        verify(produitMapper).toEntity(any(ProduitDTO.class));
        verify(produitRepository).save(any(Produit.class));
        verify(produitMapper).toDto(any(Produit.class));
    }

    @Test
    void whenFindById_existing_shouldReturnDTO() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(produitMapper.toDto(entity)).thenReturn(dto);

        Optional<ProduitDTO> found = produitService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
        verify(produitRepository).findById(1L);
        verify(produitMapper).toDto(entity);
    }

    @Test
    void whenFindById_nonExisting_shouldReturnEmpty() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProduitDTO> found = produitService.findById(99L);

        assertThat(found).isNotPresent();
        verify(produitRepository).findById(99L);
        verifyNoInteractions(produitMapper);
    }

    @Test
    void whenFindAll_shouldReturnListOfDTOs() {
        when(produitRepository.findAll()).thenReturn(Collections.singletonList(entity));
        when(produitMapper.toDto(entity)).thenReturn(dto);

        List<ProduitDTO> list = produitService.findAll();

        assertThat(list).isNotNull().hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(1L);
        verify(produitRepository).findAll();
        verify(produitMapper).toDto(entity);
    }

    @Test
    void whenDeleteById_shouldInvokeRepository() {
        Long id = 1L;
        doNothing().when(produitRepository).deleteById(id);

        produitService.deleteById(id);

        verify(produitRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(produitRepository);
    }
}
