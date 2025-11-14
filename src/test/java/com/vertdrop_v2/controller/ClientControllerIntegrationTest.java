package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.repository.ClientExpediteurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'intégration pour ClientController.
 * Ces tests vérifient le flux complet de l'API jusqu'à la base de données.
 */

// @SpringBootTest : Annotation clé qui lance le contexte complet de l'application Spring Boot.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

// @AutoConfigureMockMvc : Configure automatiquement l'outil MockMvc pour envoyer des requêtes HTTP.
@AutoConfigureMockMvc

// @ActiveProfiles("test") : Indique à Spring d'utiliser le fichier application-test.properties que nous avons créé.
@ActiveProfiles("test")

// @Transactional : Chaque méthode de test s'exécutera dans une transaction qui sera annulée (rollback) à la fin.
// C'est MAGIQUE : la base de données est automatiquement nettoyée après chaque test.
@Transactional
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Pour simuler les appels HTTP

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets en JSON et vice-versa

    @Autowired
    private ClientExpediteurRepository clientRepository; // On peut injecter les repositories pour vérifier directement la DB

    @BeforeEach
    void setUp() {
        // Avant chaque test, on s'assure que la base de données est vide.
        // @Transactional s'en charge, mais c'est une double sécurité.
        clientRepository.deleteAll();
    }

    @Test
    void whenCreateClient_thenClientIsPersisted() throws Exception {
        // --- ARRANGE ---
        // On crée le DTO à envoyer dans la requête POST
        ClientExpediteurDTO newClientDto = new ClientExpediteurDTO();
        newClientDto.setNom("Bensaltana");
        newClientDto.setPrenom("Houssam");
        newClientDto.setEmail("houssam.integration@test.com");
        newClientDto.setTelephone("1122334455");
        newClientDto.setAdresse("123 Rue de l'Intégration");

        // --- ACT (POST) & ASSERT ---
        // On exécute la requête POST pour créer le client
        MvcResult result = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClientDto)))
                // On vérifie que la réponse est correcte
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber()) // L'ID a bien été généré par la DB
                .andExpect(jsonPath("$.nom", is("Bensaltana")))
                .andReturn();

        // On extrait l'ID généré de la réponse JSON
        String responseBody = result.getResponse().getContentAsString();
        ClientExpediteurDTO createdClient = objectMapper.readValue(responseBody, ClientExpediteurDTO.class);
        Long createdClientId = createdClient.getId();

        // --- ASSERT (Vérification directe en base de données - TASK 3.3) ---
        // On vérifie que le client existe bien dans la base de données H2
        assertThat(clientRepository.findById(createdClientId)).isPresent();
        assertThat(clientRepository.count()).isEqualTo(1);

        // --- ACT (GET) & ASSERT (Vérification du scénario complet - TASK 3.5) ---
        // On appelle l'endpoint GET pour récupérer le client qu'on vient de créer
        mockMvc.perform(get("/api/clients/" + createdClientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdClientId.intValue())))
                .andExpect(jsonPath("$.email", is("houssam.integration@test.com")));
    }

    @Test
    void whenGetClientById_withNonExistingId_shouldReturnNotFound() throws Exception {
        // --- ARRANGE ---
        // La base de données est vide grâce à @Transactional et setUp()

        // --- ACT & ASSERT ---
        // On demande un ID qui n'existe pas
        mockMvc.perform(get("/api/clients/999"))
                .andExpect(status().isNotFound()); // On vérifie qu'on reçoit bien une erreur 404
    }
}