package com.vertdrop_v2.service;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.mapper.ClientExpediteurMapper;
import com.vertdrop_v2.repository.ClientExpediteurRepository;
import com.vertdrop_v2.service.impl.ClientServiceImpl;
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
class ClientServiceImplTest {

    // On mock les dépendances
    @Mock
    private ClientExpediteurRepository clientRepository;

    @Mock
    private ClientExpediteurMapper clientMapper;

    // On injecte les mocks dans la classe à tester
    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientExpediteur clientEntity;
    private ClientExpediteurDTO clientDTO;

    @BeforeEach
    void setUp() {
        // Objets réutilisables pour les tests
        clientEntity = new ClientExpediteur();
        clientEntity.setId(1L);
        clientEntity.setNom("Bensaltana");
        clientEntity.setPrenom("Houssam");

        clientDTO = new ClientExpediteurDTO();
        clientDTO.setId(1L);
        clientDTO.setNom("Bensaltana");
        clientDTO.setPrenom("Houssam");
    }

    @Test
    void whenSaveClient_shouldReturnSavedClientDTO() {
        // Arrange : Définir le comportement des mocks
        when(clientMapper.toEntity(any(ClientExpediteurDTO.class))).thenReturn(clientEntity);
        when(clientRepository.save(any(ClientExpediteur.class))).thenReturn(clientEntity);
        when(clientMapper.toDto(any(ClientExpediteur.class))).thenReturn(clientDTO);

        // Act : Appeler la méthode à tester
        ClientExpediteurDTO savedDto = clientService.save(new ClientExpediteurDTO());

        // Assert : Vérifier le résultat
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isEqualTo(1L);
        assertThat(savedDto.getNom()).isEqualTo("Bensaltana");
    }

    @Test
    void whenFindById_withExistingId_shouldReturnClientDTO() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDto(clientEntity)).thenReturn(clientDTO);

        // Act
        Optional<ClientExpediteurDTO> foundDto = clientService.findById(1L);

        // Assert
        assertThat(foundDto).isPresent();
        assertThat(foundDto.get().getId()).isEqualTo(1L);
    }

    @Test
    void whenFindById_withNonExistingId_shouldReturnEmptyOptional() {
        // Arrange
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<ClientExpediteurDTO> foundDto = clientService.findById(99L);

        // Assert
        assertThat(foundDto).isNotPresent();
    }

    @Test
    void whenFindAll_shouldReturnListOfClientDTOs() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(Collections.singletonList(clientEntity));
        when(clientMapper.toDto(clientEntity)).thenReturn(clientDTO);

        // Act
        List<ClientExpediteurDTO> dtoList = clientService.findAll();

        // Assert
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).hasSize(1);
        assertThat(dtoList.get(0).getPrenom()).isEqualTo("Houssam");
    }

    @Test
    void whenDeleteById_shouldCallRepositoryDelete() {
        // Arrange
        Long clientId = 1L;
        doNothing().when(clientRepository).deleteById(clientId);

        clientService.deleteById(clientId);

        verify(clientRepository, times(1)).deleteById(clientId);
    }
}