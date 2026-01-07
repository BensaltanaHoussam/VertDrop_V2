package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.entity.Produit;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-06T14:11:47+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Microsoft)"
)
@Component
public class ProduitMapperImpl implements ProduitMapper {

    @Override
    public ProduitDTO toDto(Produit entity) {
        if ( entity == null ) {
            return null;
        }

        ProduitDTO produitDTO = new ProduitDTO();

        produitDTO.setId( entity.getId() );
        produitDTO.setNom( entity.getNom() );
        produitDTO.setCategorie( entity.getCategorie() );
        produitDTO.setPoids( entity.getPoids() );
        produitDTO.setPrix( entity.getPrix() );

        return produitDTO;
    }

    @Override
    public Produit toEntity(ProduitDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Produit produit = new Produit();

        produit.setId( dto.getId() );
        produit.setNom( dto.getNom() );
        produit.setCategorie( dto.getCategorie() );
        produit.setPoids( dto.getPoids() );
        produit.setPrix( dto.getPrix() );

        return produit;
    }
}
