package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.HistoriqueLivraisonDTO;
import com.vertdrop_v2.entity.HistoriqueLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {


    HistoriqueLivraisonDTO toDto(HistoriqueLivraison entity);

    HistoriqueLivraison toEntity(HistoriqueLivraisonDTO dto);
}