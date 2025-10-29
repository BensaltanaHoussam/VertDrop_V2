package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisProduitDTO;
import com.vertdrop_v2.entity.ColisProduit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProduitMapper.class)
public interface ColisProduitMapper {

    // MapStruct is smart enough to see the 'produit' field and use the ProduitMapper
    ColisProduitDTO toDto(ColisProduit entity);

    // When mapping from DTO to Entity, we need to specify the source for 'produit'
    @Mapping(source = "produit", target = "produit")
    ColisProduit toEntity(ColisProduitDTO dto);
}