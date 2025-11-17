package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ColisCreateRequestDTO;
import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.UpdateStatusRequestDTO;
import com.vertdrop_v2.entity.StatutColis;
import com.vertdrop_v2.exception.GlobalExceptionHandler;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ColisService;
import com.vertdrop_v2.service.LivreurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test unitaire pour ColisController.
 * On utilise la même configuration stable que pour ClientControllerTest.
 *
 * 1. @WebMvcTest : Cible uniquement ColisController.
 * 2. excludeAutoConfiguration : Désactive Spring Security pour les tests.
 * 3. @Import : Charge le GlobalExceptionHandler pour une gestion correcte des erreurs HTTP.
 */
@WebMvcTest(controllers = ColisController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class ColisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // IMPORTANT : On doit mocker TOUTES les dépendances du contrôleur.
    // ColisController dépend de ColisService ET de LivreurService.
    @MockBean
    private ColisService colisService;

    @MockBean
    private LivreurService livreurService;

    private ColisDTO colisDTO;

    @BeforeEach
    void setUp() {
        colisDTO = new ColisDTO();
        colisDTO.setId(1L);
        colisDTO.setDescription("Colis de test");
        colisDTO.setStatut(StatutColis.CREE);
    }

    @Test
    void whenGetAllColis_shouldReturnPageOfColis() throws Exception {
        // Arrange
        Page<ColisDTO> page = new PageImpl<>(Collections.singletonList(colisDTO));
        when(colisService.findAll(any(), any(), any(), any())).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/colis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void whenGetColisById_withExistingId_shouldReturnColis() throws Exception {
        // Arrange
        when(colisService.findById(1L)).thenReturn(Optional.of(colisDTO));

        // Act & Assert
        mockMvc.perform(get("/api/colis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenGetColisById_withNonExistingId_shouldReturnNotFound() throws Exception {
        // Arrange
        when(colisService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/colis/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateColis_withValidData_shouldReturnCreated() throws Exception {
        // Arrange
        ColisCreateRequestDTO createRequest = new ColisCreateRequestDTO();
        // Remplir avec des données valides
        createRequest.setDescription("Nouveau colis");
        createRequest.setStatut("CREE");
        createRequest.setClientExpediteurId(1L);
        createRequest.setDestinataireId(2L);
        createRequest.setZoneId(3L);

        when(colisService.createFromRequest(any(ColisCreateRequestDTO.class))).thenReturn(colisDTO);

        // Act & Assert
        mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenUpdateColisStatus_withValidData_shouldReturnOk() throws Exception {
        // Arrange
        UpdateStatusRequestDTO statusRequest = new UpdateStatusRequestDTO();
        statusRequest.setStatut("EN_TRANSIT");
        statusRequest.setCommentaire("Parti de l'entrepôt");

        colisDTO.setStatut(StatutColis.EN_TRANSIT);
        when(colisService.updateStatus(eq(1L), eq(StatutColis.EN_TRANSIT), any(String.class))).thenReturn(colisDTO);

        // Act & Assert
        mockMvc.perform(put("/api/colis/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut", is("EN_TRANSIT")));
    }

    @Test
    void whenAssignLivreurToColis_withValidIds_shouldReturnOk() throws Exception {
        // Arrange
        when(colisService.assignLivreur(1L, 1L)).thenReturn(colisDTO);

        // Act & Assert
        mockMvc.perform(put("/api/colis/1/assign-livreur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenAssignLivreurToColis_withNonExistingColis_shouldReturnNotFound() throws Exception {
        // Arrange
        when(colisService.assignLivreur(99L, 1L)).thenThrow(new NotFoundException("Colis introuvable id=99"));

        // Act & Assert
        mockMvc.perform(put("/api/colis/99/assign-livreur/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetColisForLivreur_withNonExistingLivreur_shouldReturnNotFound() throws Exception {
        // Arrange
        // On simule le comportement du contrôleur : il appelle d'abord livreurService.findById
        when(livreurService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/colis/99/colis"))
                .andExpect(status().isNotFound());
    }
}