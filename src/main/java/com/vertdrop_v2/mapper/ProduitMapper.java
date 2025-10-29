package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.entity.Produit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitDTO toDto(Produit entity);

    Produit toEntity(ProduitDTO dto);
}