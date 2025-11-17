package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.ColisCreateRequestDTO;
import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.*;
import com.vertdrop_v2.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ColisControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ColisRepository colisRepository;
    @Autowired
    private ClientExpediteurRepository clientRepository;
    @Autowired
    private DestinataireRepository destinataireRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private ProduitRepository produitRepository;

    private ClientExpediteur client;
    private Destinataire destinataire;
    private Zone zone;

    @BeforeEach
    void setUp() {
        colisRepository.deleteAll();
        produitRepository.deleteAll();
        clientRepository.deleteAll();
        destinataireRepository.deleteAll();
        zoneRepository.deleteAll();

        // --- CORRECTION ---
        // On crée des entités complètes avec tous les champs non-nuls.
        client = new ClientExpediteur();
        client.setNom("ClientNom");
        client.setPrenom("ClientPrenom"); // Le champ manquant !
        client.setEmail("client.test@example.com");
        client.setTelephone("11111111");
        client.setAdresse("1 rue du Test");
        client = clientRepository.save(client);

        destinataire = new Destinataire();
        destinataire.setNom("DestinataireNom");
        destinataire.setPrenom("DestinatairePrenom"); // On ajoute aussi par sécurité
        destinataire.setEmail("dest.test@example.com");
        destinataire.setTelephone("22222222");
        destinataire.setAdresse("2 rue du Test");
        destinataire = destinataireRepository.save(destinataire);

        zone = new Zone();
        zone.setNom("ZoneTest");
        zone = zoneRepository.save(zone);
    }

    @Test
    void whenCreateColis_withValidDependencies_thenColisIsCreatedAndRetrievable() throws Exception {
        // --- ARRANGE ---
        ColisCreateRequestDTO createRequest = new ColisCreateRequestDTO();
        createRequest.setDescription("Colis d'intégration");
        createRequest.setPoids(BigDecimal.valueOf(2.5));
        createRequest.setVilleDestination("IntegrationVille");
        createRequest.setClientExpediteurId(client.getId());
        createRequest.setDestinataireId(destinataire.getId());
        createRequest.setZoneId(zone.getId());
        createRequest.setStatut("CREE");

        // --- ACT (POST) & ASSERT ---
        String responseContent = mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description", is("Colis d'intégration")))
                .andExpect(jsonPath("$.clientExpediteur.id", is(client.getId().intValue())))
                .andExpect(jsonPath("$.destinataire.id", is(destinataire.getId().intValue())))
                .andExpect(jsonPath("$.zone.id", is(zone.getId().intValue())))
                .andReturn().getResponse().getContentAsString();

        ColisDTO createdColis = objectMapper.readValue(responseContent, ColisDTO.class);
        Long createdColisId = createdColis.getId();

        // --- ASSERT (Vérification DB) ---
        assertThat(colisRepository.findById(createdColisId)).isPresent();

        // --- ACT (GET) & ASSERT (Scénario complet) ---
        mockMvc.perform(get("/api/colis/" + createdColisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdColisId.intValue())))
                .andExpect(jsonPath("$.description", is("Colis d'intégration")));
    }
}