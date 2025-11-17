//package com.vertdrop_v2.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vertdrop_v2.dto.LivreurDTO;
//import com.vertdrop_v2.entity.Colis;
//import com.vertdrop_v2.entity.Livreur;
//import com.vertdrop_v2.repository.ColisRepository;
//import com.vertdrop_v2.repository.LivreurRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//class LivreurControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private LivreurRepository livreurRepository;
//
//    @Autowired
//    private ColisRepository colisRepository; // Nécessaire pour le test getColisForLivreur
//
//    @BeforeEach
//    void setUp() {
//        // Ordre important : on supprime les entités dépendantes (colis) avant les entités principales (livreur)
//        colisRepository.deleteAll();
//        livreurRepository.deleteAll();
//    }
//
//    @Test
//    void whenCreateLivreur_thenLivreurIsPersisted() throws Exception {
//        // ARRANGE
//        LivreurDTO newLivreurDto = new LivreurDTO();
//        newLivreurDto.setNom("Leblanc");
//        newLivreurDto.setPrenom("Marc");
//        newLivreurDto.setTelephone("0612345678");
//
//        // ACT (POST) & ASSERT
//        String responseContent = mockMvc.perform(post("/api/livreurs")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newLivreurDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").isNumber())
//                .andExpect(jsonPath("$.nom", is("Leblanc")))
//                .andReturn().getResponse().getContentAsString();
//
//        LivreurDTO createdLivreur = objectMapper.readValue(responseContent, LivreurDTO.class);
//        Long createdId = createdLivreur.getId();
//
//        // ASSERT (DB)
//        assertThat(livreurRepository.findById(createdId)).isPresent();
//        assertThat(livreurRepository.count()).isEqualTo(1);
//    }
//
//    @Test
//    void whenGetColisForLivreur_thenReturnsAssociatedColis() throws Exception {
//        // ARRANGE
//        // 1. Créer et sauver un livreur
//        Livreur livreur = new Livreur();
//        livreur.setNom("Test");
//        livreur.setPrenom("Livreur");
//        livreur.setTelephone("123");
//        Livreur savedLivreur = livreurRepository.save(livreur);
//
//        // 2. Créer et sauver un colis assigné à ce livreur
//        Colis colis = new Colis();
//        colis.setDescription("Colis pour le test d'intégration");
//        colis.setLivreur(savedLivreur);
//        colisRepository.save(colis);
//
//        // S'assurer que la base de données est dans l'état attendu
//        assertThat(livreurRepository.count()).isEqualTo(1);
//        assertThat(colisRepository.count()).isEqualTo(1);
//
//        // ACT & ASSERT
//        mockMvc.perform(get("/api/livreurs/" + savedLivreur.getId() + "/colis"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1))) // On s'attend à une liste contenant 1 colis
//                .andExpect(jsonPath("$[0].description", is("Colis pour le test d'intégration")));
//    }
//}