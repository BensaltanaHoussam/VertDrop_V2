package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ColisProduitDTO;
import com.vertdrop_v2.entity.ColisProduit;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-20T10:28:36+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
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

        colisProduitDTO.setProduit( produitMapper.toDto( entity.getProduit() ) );
        colisProduitDTO.setQuantite( entity.getQuantite() );
        colisProduitDTO.setPrix( entity.getPrix() );
        colisProduitDTO.setDateAjout( entity.getDateAjout() );

        return colisProduitDTO;
    }

    @Override
    public ColisProduit toEntity(ColisProduitDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ColisProduit colisProduit = new ColisProduit();

        colisProduit.setProduit( produitMapper.toEntity( dto.getProduit() ) );
        colisProduit.setQuantite( dto.getQuantite() );
        colisProduit.setPrix( dto.getPrix() );
        colisProduit.setDateAjout( dto.getDateAjout() );

        return colisProduit;
    }
}
