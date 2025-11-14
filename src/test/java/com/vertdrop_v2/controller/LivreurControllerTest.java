package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.dto.LivreurDTO;
import com.vertdrop_v2.entity.StatutLivreur;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de tests complète pour LivreurController.
 */
@WebMvcTest(controllers = LivreurController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class LivreurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Le LivreurController dépend de deux services, il faut mocker les deux.
    @MockBean
    private LivreurService livreurService;

    @MockBean
    private ColisService colisService;

    private LivreurDTO validLivreurDTO;

    @BeforeEach
    void setUp() {
        // Création d'un DTO valide pour les tests
        validLivreurDTO = new LivreurDTO();
        validLivreurDTO.setId(1L);
        validLivreurDTO.setNom("Martin");
        validLivreurDTO.setPrenom("Paul");
        validLivreurDTO.setTelephone("0611223344");
        validLivreurDTO.setStatut(StatutLivreur.ACTIF);
    }

    @Test
    void whenGetAllLivreurs_shouldReturnListOfLivreurs() throws Exception {
        when(livreurService.findAll()).thenReturn(Collections.singletonList(validLivreurDTO));

        mockMvc.perform(get("/api/livreurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Martin")));
    }

    @Test
    void whenGetLivreurById_withExistingId_shouldReturnLivreur() throws Exception {
        when(livreurService.findById(1L)).thenReturn(Optional.of(validLivreurDTO));

        mockMvc.perform(get("/api/livreurs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenGetLivreurById_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(livreurService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/livreurs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateLivreur_withValidData_shouldReturnCreated() throws Exception {
        LivreurDTO inputDTO = new LivreurDTO();
        inputDTO.setNom("Nouveau");
        inputDTO.setPrenom("Livreur");
        inputDTO.setTelephone("0655443322");
        inputDTO.setStatut(StatutLivreur.ACTIF);

        when(livreurService.save(any(LivreurDTO.class))).thenReturn(validLivreurDTO);

        mockMvc.perform(post("/api/livreurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenUpdateLivreur_withValidData_shouldReturnOk() throws Exception {
        Long livreurId = 1L;
        validLivreurDTO.setId(livreurId);

        when(livreurService.findById(livreurId)).thenReturn(Optional.of(validLivreurDTO));
        when(livreurService.save(any(LivreurDTO.class))).thenReturn(validLivreurDTO);

        mockMvc.perform(put("/api/livreurs/" + livreurId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUpdateLivreur_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long nonExistingId = 99L;
        validLivreurDTO.setId(nonExistingId);

        when(livreurService.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/livreurs/" + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLivreurDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteLivreur_withExistingId_shouldReturnNoContent() throws Exception {
        when(livreurService.findById(1L)).thenReturn(Optional.of(validLivreurDTO));
        doNothing().when(livreurService).deleteById(1L);

        mockMvc.perform(delete("/api/livreurs/1"))
                .andExpect(status().isNoContent());

        verify(livreurService, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteLivreur_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(livreurService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/livreurs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetColisForLivreur_withExistingLivreur_shouldReturnColisList() throws Exception {
        ColisDTO colis = new ColisDTO();
        colis.setId(10L);

        when(livreurService.findById(1L)).thenReturn(Optional.of(validLivreurDTO));
        when(colisService.findByLivreurId(1L)).thenReturn(Collections.singletonList(colis));

        mockMvc.perform(get("/api/livreurs/1/colis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(10)));
    }

    @Test
    void whenGetColisForLivreur_withNonExistingLivreur_shouldReturnNotFound() throws Exception {
        when(livreurService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/livreurs/99/colis"))
                .andExpect(status().isNotFound());
    }
}