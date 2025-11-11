package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.mapper.ColisMapper;
import com.vertdrop_v2.repository.ColisRepository;
import com.vertdrop_v2.service.impl.ColisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColisServiceImplTest {

    @Mock
    private ColisRepository colisRepository;

    @Mock
    private ColisMapper colisMapper;




    @InjectMocks
    private ColisServiceImpl colisService;

    private Colis colis;
    private ColisDTO colisDTO;

    @BeforeEach
    void setUp() {
        colis = new Colis();
        colis.setId(1L);
        colis.setDescription("Test colis");

        colisDTO = new ColisDTO();
        colisDTO.setId(1L);
        colisDTO.setDescription("Test colis");
    }

    @Test
    void whenFindById_withExistingId_shouldReturnColisDTO() {
        when(colisRepository.findById(1L)).thenReturn(Optional.of(colis));

        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisDTO);
        Optional<ColisDTO> foundColis = colisService.findById(1L);

        assertThat(foundColis).isPresent();
        assertThat(foundColis.get().getId()).isEqualTo(1L);
        assertThat(foundColis.get().getDescription()).isEqualTo("Test colis");
    }

    @Test
    void whenFindById_withNonExistingId_shouldReturnEmptyOptional() {

        when(colisRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ColisDTO> foundColis = colisService.findById(99L);

        assertThat(foundColis).isNotPresent(); // On vérifie que l'Optional est bien vide
    }
}