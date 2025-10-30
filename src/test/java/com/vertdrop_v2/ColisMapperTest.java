package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.Colis;
import com.vertdrop_v2.entity.StatutColis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

// Load the Spring context to be able to inject the mapper
@SpringBootTest
class ColisMapperTest {

    // Spring injects the mapper implementation that MapStruct generated
    @Autowired
    private ColisMapper colisMapper;

    @Test
    void shouldMapEntityToDto() {
        // --- 1. ARRANGE: Create a complex entity object ---
        ClientExpediteur sender = new ClientExpediteur();
        sender.setId(1L);
        sender.setNom("Bensaltana");
        sender.setPrenom("Houssam");

        Colis colisEntity = new Colis();
        colisEntity.setId(101L);
        colisEntity.setDescription("Test Parcel");
        colisEntity.setStatut(StatutColis.EN_TRANSIT);
        colisEntity.setPoids(new BigDecimal("5.5"));
        colisEntity.setClientExpediteur(sender); // Set the nested object

        // --- 2. ACT: Use the mapper to convert the entity to a DTO ---
        ColisDTO colisDto = colisMapper.toDto(colisEntity);

        // --- 3. ASSERT: Check if the DTO has the correct values ---
        assertThat(colisDto).isNotNull();
        assertThat(colisDto.getId()).isEqualTo(101L);
        assertThat(colisDto.getDescription()).isEqualTo("Test Parcel");
        assertThat(colisDto.getStatut()).isEqualTo(StatutColis.EN_TRANSIT);

        // Crucially, check the nested DTO
        assertThat(colisDto.getClientExpediteur()).isNotNull();
        assertThat(colisDto.getClientExpediteur().getNom()).isEqualTo("Bensaltana");
        assertThat(colisDto.getClientExpediteur().getId()).isEqualTo(1L);

        System.out.println("Successfully mapped Entity to DTO: " + colisDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        // --- 1. ARRANGE: Create a DTO object ---
        ColisDTO colisDto = new ColisDTO();
        colisDto.setId(202L);
        colisDto.setDescription("DTO to Entity Test");
        colisDto.setStatut(StatutColis.LIVRE);

        // --- 2. ACT: Use the mapper to convert the DTO to an entity ---
        Colis colisEntity = colisMapper.toEntity(colisDto);

        // --- 3. ASSERT: Check if the entity has the correct values ---
        assertThat(colisEntity).isNotNull();
        assertThat(colisEntity.getId()).isEqualTo(202L);
        assertThat(colisEntity.getDescription()).isEqualTo("DTO to Entity Test");
        assertThat(colisEntity.getStatut()).isEqualTo(StatutColis.LIVRE);

        System.out.println("Successfully mapped DTO to Entity: " + colisEntity);
    }
}