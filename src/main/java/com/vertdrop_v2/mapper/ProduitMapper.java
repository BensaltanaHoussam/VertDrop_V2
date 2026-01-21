package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    @Mapping(target = "clientExpediteurId", source = "clientExpediteur.id")
    ProduitDTO toDto(Produit entity);

    @Mapping(target = "clientExpediteur.id", source = "clientExpediteurId")
    Produit toEntity(ProduitDTO dto);
}