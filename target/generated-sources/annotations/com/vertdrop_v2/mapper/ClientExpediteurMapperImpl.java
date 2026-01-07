package com.vertdrop_v2.mapper;

import com.vertdrop_v2.dto.ClientExpediteurDTO;
import com.vertdrop_v2.entity.ClientExpediteur;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-06T14:11:47+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Microsoft)"
)
@Component
public class ClientExpediteurMapperImpl implements ClientExpediteurMapper {

    @Override
    public ClientExpediteurDTO toDto(ClientExpediteur entity) {
        if ( entity == null ) {
            return null;
        }

        ClientExpediteurDTO clientExpediteurDTO = new ClientExpediteurDTO();

        clientExpediteurDTO.setId( entity.getId() );
        clientExpediteurDTO.setNom( entity.getNom() );
        clientExpediteurDTO.setPrenom( entity.getPrenom() );
        clientExpediteurDTO.setEmail( entity.getEmail() );
        clientExpediteurDTO.setTelephone( entity.getTelephone() );
        clientExpediteurDTO.setAdresse( entity.getAdresse() );

        return clientExpediteurDTO;
    }

    @Override
    public ClientExpediteur toEntity(ClientExpediteurDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ClientExpediteur clientExpediteur = new ClientExpediteur();

        clientExpediteur.setId( dto.getId() );
        clientExpediteur.setNom( dto.getNom() );
        clientExpediteur.setPrenom( dto.getPrenom() );
        clientExpediteur.setEmail( dto.getEmail() );
        clientExpediteur.setTelephone( dto.getTelephone() );
        clientExpediteur.setAdresse( dto.getAdresse() );

        return clientExpediteur;
    }
}
