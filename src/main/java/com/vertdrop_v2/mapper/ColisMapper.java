package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisDTO;
import com.vertdrop_v2.entity.Colis;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        ClientExpediteurMapper.class,
        DestinataireMapper.class,
        LivreurMapper.class,
        ZoneMapper.class,
        HistoriqueLivraisonMapper.class,
        ColisProduitMapper.class
})
public interface ColisMapper {

    ColisDTO toDto(Colis entity);

    Colis toEntity(ColisDTO dto);
}