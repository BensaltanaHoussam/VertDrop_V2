//package com.vertdrop_v2.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.vertdrop_v2.dto.ZoneDTO;
//import com.vertdrop_v2.repository.ZoneRepository;
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
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//class ZoneControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private ZoneRepository zoneRepository;
//
//    @BeforeEach
//    void setUp() {
//        zoneRepository.deleteAll();
//    }
//
//    @Test
//    void whenCreateZone_thenZoneIsPersisted() throws Exception {
//        // ARRANGE
//        ZoneDTO newDto = new ZoneDTO();
//        newDto.setNom("Lyon Centre");
//        newDto.setCodePostal("69002");
//
//        // ACT & ASSERT
//        mockMvc.perform(post("/api/zones")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").isNumber())
//                .andExpect(jsonPath("$.nom", is("Lyon Centre")));
//
//        // ASSERT (DB)
//        assertThat(zoneRepository.count()).isEqualTo(1);
//    }
//}