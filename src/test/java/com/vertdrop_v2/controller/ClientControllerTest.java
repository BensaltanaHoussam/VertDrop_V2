package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.exception.GlobalExceptionHandler;
import com.vertdrop_v2.exception.NotFoundException;
import com.vertdrop_v2.service.ClientService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Version de test finale pour ClientController.
 * Cette configuration est conçue pour être stable et résoudre les problèmes de "404 vs 500".
 *
 * 1. @WebMvcTest(controllers = ClientController.class, ... ) : Cible UNIQUEMENT le ClientController pour un test rapide et isolé.
 * 2. excludeAutoConfiguration = {SecurityAutoConfiguration.class} : Désactive Spring Security, qui peut interférer avec la gestion des exceptions dans les tests.
 * 3. @Import(GlobalExceptionHandler.class) : Force le chargement de votre gestionnaire d'exceptions unifié pour qu'il puisse convertir les exceptions (NotFoundException) en codes HTTP corrects (404).
 */
@WebMvcTest(controllers = ClientController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientExpediteurDTO clientDTO;

    @BeforeEach
    void setUp() {
        clientDTO = new ClientExpediteurDTO();
        clientDTO.setId(1L);
        clientDTO.setNom("Bensaltana");
        clientDTO.setPrenom("Houssam");
        clientDTO.setEmail("houssam@example.com");
        clientDTO.setTelephone("123456789");
        clientDTO.setAdresse("123 Rue Test");
    }

    @Test
    void whenGetAllClients_shouldReturnListOfClients() throws Exception {
        when(clientService.findAll()).thenReturn(Collections.singletonList(clientDTO));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom", is("Bensaltana")));
    }

    @Test
    void whenGetClientById_withExistingId_shouldReturnClient() throws Exception {
        when(clientService.findById(1L)).thenReturn(Optional.of(clientDTO));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.prenom", is("Houssam")));
    }

    @Test
    void whenGetClientById_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(clientService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    void whenCreateClient_withValidData_shouldReturnCreated() throws Exception {
        ClientExpediteurDTO inputDTO = new ClientExpediteurDTO();
        inputDTO.setNom("Nouveau");
        inputDTO.setPrenom("Client");
        inputDTO.setEmail("nouveau@example.com");
        inputDTO.setTelephone("000000000");
        inputDTO.setAdresse("Nouvelle Adresse");

        when(clientService.save(any(ClientExpediteurDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenCreateClient_withInvalidData_shouldReturnBadRequest() throws Exception {
        ClientExpediteurDTO invalidDTO = new ClientExpediteurDTO();
        invalidDTO.setNom(""); // Champ invalide

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateClient_withExistingId_shouldReturnOk() throws Exception {
        when(clientService.findById(1L)).thenReturn(Optional.of(clientDTO));
        when(clientService.save(any(ClientExpediteurDTO.class))).thenReturn(clientDTO);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Bensaltana")));
    }

    @Test
    void whenUpdateClient_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(clientService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteClient_withExistingId_shouldReturnNoContent() throws Exception {
        when(clientService.findById(1L)).thenReturn(Optional.of(clientDTO));
        doNothing().when(clientService).deleteById(1L);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());

        verify(clientService, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteClient_withNonExistingId_shouldReturnNotFound() throws Exception {
        when(clientService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound());
    }
}