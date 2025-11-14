package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.exception.GlobalExceptionHandler;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.DestinataireService;
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
 * Suite de tests complète pour DestinataireController.
 * Utilise la configuration de test stable :
 * 1. @WebMvcTest pour cibler le contrôleur.
 * 2. excludeAutoConfiguration pour désactiver la sécurité.
 * 3. @Import pour charger le gestionnaire d'exceptions.
 */
@WebMvcTest(controllers = DestinataireController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class DestinataireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DestinataireService destinataireService;

    private DestinataireDTO validDestinataireDTO;

    @BeforeEach
    void setUp() {
        // Création d'un DTO complet et valide pour passer les validations @Valid
        validDestinataireDTO = new DestinataireDTO();
        validDestinataireDTO.setId(1L);
        validDestinataireDTO.setNom("Dupont");
        validDestinataireDTO.setPrenom("Jean");
        validDestinataireDTO.setEmail("jean.dupont@example.com");
        validDestinataireDTO.setTelephone("0123456789");
        validDestinataireDTO.setAdresse("10 Rue de la Paix");
    }

    @Test
    void whenGetAllDestinataires_shouldReturnListOfDestinataires() throws Exception {
        when(destinataireService.findAll()).thenReturn(Collections.singletonList(validDestinataireDTO));

        mockMvc.perform(get("/api/destinataires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Dupont")));
    }

    @Test
    void whenGetDestinataireById_withExistingId_shouldReturnDestinataire() throws Exception {
        when(destinataireService.findById(1L)).thenReturn(Optional.of(validDestinataireDTO));

        mockMvc.perform(get("/api/destinataires/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenGetDestinataireById_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(destinataireService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/destinataires/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateDestinataire_withValidData_shouldReturnCreated() throws Exception {
        // DTO d'entrée sans ID
        DestinataireDTO inputDTO = new DestinataireDTO();
        inputDTO.setNom("Nouveau");
        inputDTO.setPrenom("Destinataire");
        inputDTO.setEmail("nouveau@dest.com");
        inputDTO.setTelephone("9876543210");
        inputDTO.setAdresse("Quelque part");

        when(destinataireService.save(any(DestinataireDTO.class))).thenReturn(validDestinataireDTO);

        mockMvc.perform(post("/api/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenUpdateDestinataire_withValidData_shouldReturnOk() throws Exception {
        Long destinataireId = 1L;
        validDestinataireDTO.setId(destinataireId);

        when(destinataireService.findById(destinataireId)).thenReturn(Optional.of(validDestinataireDTO));
        when(destinataireService.save(any(DestinataireDTO.class))).thenReturn(validDestinataireDTO);

        mockMvc.perform(put("/api/destinataires/" + destinataireId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUpdateDestinataire_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long nonExistingId = 99L;
        validDestinataireDTO.setId(nonExistingId); // Assurer la cohérence pour la validation

        when(destinataireService.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/destinataires/" + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDestinataireDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteDestinataire_withExistingId_shouldReturnNoContent() throws Exception {
        when(destinataireService.findById(1L)).thenReturn(Optional.of(validDestinataireDTO));
        doNothing().when(destinataireService).deleteById(1L);

        mockMvc.perform(delete("/api/destinataires/1"))
                .andExpect(status().isNoContent());

        verify(destinataireService, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteDestinataire_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(destinataireService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/destinataires/99"))
                .andExpect(status().isNotFound());
    }
}