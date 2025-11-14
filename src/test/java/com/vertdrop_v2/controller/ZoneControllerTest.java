package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ZoneDTO;
import com.vertdrop_v2.exception.GlobalExceptionHandler;
import com.vertdrop_v2.service.ZoneService;
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
 * Suite de tests complète pour ZoneController.
 */
@WebMvcTest(controllers = ZoneController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ZoneService zoneService;

    private ZoneDTO validZoneDTO;

    @BeforeEach
    void setUp() {
        // Création d'un DTO valide qui respecte les contraintes de validation (@NotBlank sur nom)
        validZoneDTO = new ZoneDTO();
        validZoneDTO.setId(1L);
        validZoneDTO.setNom("Paris Centre");
        validZoneDTO.setCodePostal("75001");
    }

    @Test
    void whenGetAllZones_shouldReturnListOfZones() throws Exception {
        when(zoneService.findAll()).thenReturn(Collections.singletonList(validZoneDTO));

        mockMvc.perform(get("/api/zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Paris Centre")));
    }

    @Test
    void whenGetZoneById_withExistingId_shouldReturnZone() throws Exception {
        when(zoneService.findById(1L)).thenReturn(Optional.of(validZoneDTO));

        mockMvc.perform(get("/api/zones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenGetZoneById_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(zoneService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/zones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateZone_withValidData_shouldReturnCreated() throws Exception {
        // DTO d'entrée sans ID
        ZoneDTO inputDTO = new ZoneDTO();
        inputDTO.setNom("Marseille Vieux-Port");
        inputDTO.setCodePostal("13001");

        when(zoneService.save(any(ZoneDTO.class))).thenReturn(validZoneDTO);

        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenUpdateZone_withValidData_shouldReturnOk() throws Exception {
        Long zoneId = 1L;
        validZoneDTO.setId(zoneId);

        when(zoneService.findById(zoneId)).thenReturn(Optional.of(validZoneDTO));
        when(zoneService.save(any(ZoneDTO.class))).thenReturn(validZoneDTO);

        mockMvc.perform(put("/api/zones/" + zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void whenUpdateZone_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long nonExistingId = 99L;
        validZoneDTO.setId(nonExistingId);

        when(zoneService.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/zones/" + nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteZone_withExistingId_shouldReturnNoContent() throws Exception {
        when(zoneService.findById(1L)).thenReturn(Optional.of(validZoneDTO));
        doNothing().when(zoneService).deleteById(1L);

        mockMvc.perform(delete("/api/zones/1"))
                .andExpect(status().isNoContent());

        verify(zoneService, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteZone_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(zoneService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/zones/99"))
                .andExpect(status().isNotFound());
    }
}