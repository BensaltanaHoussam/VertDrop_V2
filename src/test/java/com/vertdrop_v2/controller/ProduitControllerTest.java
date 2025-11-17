package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.exception.GlobalExceptionHandler;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ProduitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de tests complète pour ProduitController.
 */
@WebMvcTest(controllers = ProduitController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProduitService produitService;

    private ProduitDTO validProduitDTO;

    @BeforeEach
    void setUp() {
        // Création d'un DTO complet et valide pour les tests.
        validProduitDTO = new ProduitDTO();
        validProduitDTO.setId(1L);
        validProduitDTO.setNom("Livre");
        validProduitDTO.setCategorie("Éducation");
        validProduitDTO.setPoids(BigDecimal.valueOf(0.8));
        validProduitDTO.setPrix(BigDecimal.valueOf(19.99));
    }

    @Test
    void whenGetAllProduits_shouldReturnListOfProduits() throws Exception {
        when(produitService.findAll()).thenReturn(Collections.singletonList(validProduitDTO));

        mockMvc.perform(get("/api/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Livre")));
    }

    @Test
    void whenGetProduitById_withExistingId_shouldReturnProduit() throws Exception {
        when(produitService.findById(1L)).thenReturn(Optional.of(validProduitDTO));

        mockMvc.perform(get("/api/produits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenGetProduitById_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(produitService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/produits/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateProduit_withValidData_shouldReturnCreated() throws Exception {
        // DTO d'entrée sans ID
        ProduitDTO inputDTO = new ProduitDTO();
        inputDTO.setNom("Stylo");
        inputDTO.setCategorie("Fournitures");
        inputDTO.setPoids(BigDecimal.valueOf(0.1));
        inputDTO.setPrix(BigDecimal.valueOf(1.50));

        when(produitService.save(any(ProduitDTO.class))).thenReturn(validProduitDTO);

        mockMvc.perform(post("/api/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenUpdateProduit_withValidData_shouldReturnOk() throws Exception {
        Long produitId = 1L;
        validProduitDTO.setId(produitId);

        when(produitService.findById(produitId)).thenReturn(Optional.of(validProduitDTO));
        when(produitService.save(any(ProduitDTO.class))).thenReturn(validProduitDTO);

        mockMvc.perform(put("/api/produits/" + produitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUpdateProduit_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long nonExistingId = 99L;
        validProduitDTO.setId(nonExistingId);

        when(produitService.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/produits/" + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduitDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteProduit_withExistingId_shouldReturnNoContent() throws Exception {
        when(produitService.findById(1L)).thenReturn(Optional.of(validProduitDTO));
        doNothing().when(produitService).deleteById(1L);

        mockMvc.perform(delete("/api/produits/1"))
                .andExpect(status().isNoContent());

        verify(produitService, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteProduit_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(produitService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/produits/99"))
                .andExpect(status().isNotFound());
    }
}