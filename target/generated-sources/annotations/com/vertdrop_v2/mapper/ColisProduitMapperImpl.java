package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisProduitDTO;
import com.vertdrop_v2.entity.ColisProduit;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-05T11:56:32+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ColisProduitMapperImpl implements ColisProduitMapper {

    @Autowired
    private ProduitMapper produitMapper;

    @Override
    public ColisProduitDTO toDto(ColisProduit entity) {
        if ( entity == null ) {
            return null;
        }

        ColisProduitDTO colisProduitDTO = new ColisProduitDTO();

        colisProduitDTO.setDateAjout( entity.getDateAjout() );
        colisProduitDTO.setPrix( entity.getPrix() );
        colisProduitDTO.setProduit( produitMapper.toDto( entity.getProduit() ) );
        colisProduitDTO.setQuantite( entity.getQuantite() );

        return colisProduitDTO;
    }

    @Override
    public ColisProduit toEntity(ColisProduitDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ColisProduit colisProduit = new ColisProduit();

        colisProduit.setProduit( produitMapper.toEntity( dto.getProduit() ) );
        colisProduit.setDateAjout( dto.getDateAjout() );
        colisProduit.setPrix( dto.getPrix() );
        colisProduit.setQuantite( dto.getQuantite() );

        return colisProduit;
    }
}
