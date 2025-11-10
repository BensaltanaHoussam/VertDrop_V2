package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.Colis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        ClientExpediteurMapper.class,
        DestinataireMapper.class,
        LivreurMapper.class,
        ZoneMapper.class,
        HistoriqueLivraisonMapper.class,
        ColisProduitMapper.class
})
public interface ColisMapper {

    // Conserve le mapping vers DTO, incluant l'historique et les produits
    ColisDTO toDto(Colis entity);

    // On ignore les collections pour les gérer manuellement dans le service
    @Mapping(target = "historiqueLivraisons", ignore = true)
    @Mapping(target = "colisProduits", ignore = true)
    Colis toEntity(ColisDTO dto);
}
