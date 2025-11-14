package com.vertdrop_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vertdrop_v2.dto.DestinataireDTO;
import com.vertdrop_v2.repository.DestinataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DestinataireControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DestinataireRepository destinataireRepository;

    @BeforeEach
    void setUp() {
        destinataireRepository.deleteAll();
    }

    @Test
    void whenCreateDestinataire_thenDestinataireIsPersisted() throws Exception {
        // ARRANGE
        DestinataireDTO newDto = new DestinataireDTO();
        newDto.setNom("Durand");
        newDto.setPrenom("Sophie");
        newDto.setEmail("sophie.durand@test.com");
        newDto.setTelephone("0987654321");
        newDto.setAdresse("456 Avenue des Tests");

        // ACT & ASSERT
        mockMvc.perform(post("/api/destinataires")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nom", is("Durand")));

        // ASSERT (DB)
        assertThat(destinataireRepository.count()).isEqualTo(1);
        assertThat(destinataireRepository.findAll().get(0).getEmail()).isEqualTo("sophie.durand@test.com");
    }
}