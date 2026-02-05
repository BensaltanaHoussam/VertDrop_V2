package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ProduitDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import com.vertdrop_v2.entity.Produit;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-05T11:56:32+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ProduitMapperImpl implements ProduitMapper {

    @Override
    public ProduitDTO toDto(Produit entity) {
        if ( entity == null ) {
            return null;
        }

        ProduitDTO produitDTO = new ProduitDTO();

        produitDTO.setClientExpediteurId( entityClientExpediteurId( entity ) );
        produitDTO.setCategorie( entity.getCategorie() );
        produitDTO.setId( entity.getId() );
        produitDTO.setNom( entity.getNom() );
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

        produit.setClientExpediteur( produitDTOToClientExpediteur( dto ) );
        produit.setCategorie( dto.getCategorie() );
        produit.setId( dto.getId() );
        produit.setNom( dto.getNom() );
        produit.setPoids( dto.getPoids() );
        produit.setPrix( dto.getPrix() );

        return produit;
    }

    private Long entityClientExpediteurId(Produit produit) {
        if ( produit == null ) {
            return null;
        }
        ClientExpediteur clientExpediteur = produit.getClientExpediteur();
        if ( clientExpediteur == null ) {
            return null;
        }
        Long id = clientExpediteur.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected ClientExpediteur produitDTOToClientExpediteur(ProduitDTO produitDTO) {
        if ( produitDTO == null ) {
            return null;
        }

        ClientExpediteur clientExpediteur = new ClientExpediteur();

        clientExpediteur.setId( produitDTO.getClientExpediteurId() );

        return clientExpediteur;
    }
}
